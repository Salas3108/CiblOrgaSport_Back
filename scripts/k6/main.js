import http from 'k6/http';
import { check, group, sleep } from 'k6';

const vus = Number(__ENV.VUS || 10);
const rampUp = __ENV.RAMP_UP || '30s';
const steady = __ENV.STEADY || '90s';
const rampDown = __ENV.RAMP_DOWN || '30s';

const authBase = __ENV.AUTH_BASE || 'http://localhost:8080';
const eventBase = __ENV.EVENT_BASE || 'http://localhost:8084';
const participantsBase = __ENV.PARTICIPANTS_BASE || 'http://localhost:8087';
const resultatsBase = __ENV.RESULTATS_BASE || 'http://localhost:8088';
const abonnementBase = __ENV.ABONNEMENT_BASE || 'http://localhost:8082';
const billetterieBase = __ENV.BILLETTERIE_BASE || 'http://localhost:8081';
const incidentBase = __ENV.INCIDENT_BASE || 'http://localhost:8083';
const lieuBase = __ENV.LIEU_BASE || 'http://localhost:8090';
const notificationsBase = __ENV.NOTIFICATIONS_BASE || 'http://localhost:8089';
const analyticsBase = __ENV.ANALYTICS_BASE || 'http://localhost:8091';
const geolocationBase = __ENV.GEOLOCATION_BASE || 'http://localhost:8092';
const volunteerBase = __ENV.VOLUNTEER_BASE || 'http://localhost:8093';
const username = __ENV.AUTH_USERNAME || 'admin3';
const password = __ENV.AUTH_PASSWORD || '123456';

export const options = {
  stages: [
    { duration: rampUp, target: vus },
    { duration: steady, target: vus },
    { duration: rampDown, target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1200', 'p(99)<2500'],
    checks: ['rate>0.95'],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

function login() {
  const payload = JSON.stringify({ username, password });

  const res = http.post(`${authBase}/auth/login`, payload, {
    headers: { 'Content-Type': 'application/json' },
    tags: { endpoint: 'auth_login' },
  });

  const ok = check(res, {
    'login: status is 200': (r) => r.status === 200,
    'login: has token': (r) => {
      try {
        const body = r.json();
        return Boolean(body && body.token);
      } catch (_) {
        return false;
      }
    },
  });

  if (!ok) {
    return null;
  }

  return res.json('token');
}

function getMe(token) {
  const res = http.get(`${authBase}/auth/me`, {
    headers: { Authorization: `Bearer ${token}` },
    tags: { endpoint: 'auth_me' },
  });

  check(res, {
    'me: status is 200': (r) => r.status === 200,
  });
}

function getWithFallback(urls, params, acceptedStatuses = [200]) {
  let lastRes = null;

  for (const url of urls) {
    const res = http.get(url, params);
    lastRes = res;

    if (acceptedStatuses.includes(res.status)) {
      return res;
    }
  }

  return lastRes;
}

function discoverEpreuveId(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const params = { headers, tags: { endpoint: 'event_epreuves_list' } };

  const res = getWithFallback(
    [`${eventBase}/epreuves`, `${eventBase}/api/epreuves`],
    params,
    [200, 204]
  );

  check(res, {
    'event: epreuves list reachable': (r) => r && (r.status === 200 || r.status === 204),
  });

  if (!res || res.status !== 200) {
    return null;
  }

  try {
    const body = res.json();
    if (!Array.isArray(body) || body.length === 0) {
      return null;
    }
    return body[0].id || null;
  } catch (_) {
    return null;
  }
}

function runEventCriticalFlow(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const eventsRes = getWithFallback(
    [`${eventBase}/events`, `${eventBase}/api/events`],
    { headers, tags: { endpoint: 'event_events_list' } },
    [200, 204]
  );

  check(eventsRes, {
    'event: events list reachable': (r) => r && (r.status === 200 || r.status === 204),
  });

  const competitionsRes = http.get(`${eventBase}/competitions`, {
    headers,
    tags: { endpoint: 'event_competitions_list' },
  });

  check(competitionsRes, {
    'event: competitions reachable': (r) => r.status === 200 || r.status === 204 || r.status === 401 || r.status === 403,
  });
}

function runParticipantsCriticalFlow(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const assignmentsRes = getWithFallback(
    [`${participantsBase}/commissaire/epreuves/assignments`, `${participantsBase}/api/commissaire/epreuves/assignments`],
    { headers, tags: { endpoint: 'participants_epreuves_assignments' } },
    [200, 204]
  );

  check(assignmentsRes, {
    'participants: assignments reachable': (r) => r && (r.status === 200 || r.status === 204),
  });

  if (token) {
    const equipesRes = getWithFallback(
      [`${participantsBase}/commissaire/equipes`, `${participantsBase}/api/commissaire/equipes`],
      { headers, tags: { endpoint: 'participants_equipes_list' } },
      [200, 204, 403]
    );

    check(equipesRes, {
      'participants: equipes endpoint responsive': (r) => r && (r.status === 200 || r.status === 204 || r.status === 403),
    });
  }
}

function runResultatsCriticalFlow(token, epreuveId) {
  if (!epreuveId) {
    return;
  }

  const publicRes = getWithFallback(
    [
      `${resultatsBase}/api/public/resultats/epreuves/${epreuveId}`,
      `${resultatsBase}/api/resultats/public/epreuves/${epreuveId}`,
      `${resultatsBase}/resultats/public/epreuves/${epreuveId}`,
    ],
    { tags: { endpoint: 'resultats_public_epreuve' } },
    [200, 204]
  );

  check(publicRes, {
    'resultats: public classement reachable': (r) => r && (r.status === 200 || r.status === 204),
  });

  if (token) {
    const commissaireRes = getWithFallback(
      [
        `${resultatsBase}/api/resultats/commissaire/epreuves/${epreuveId}`,
        `${resultatsBase}/resultats/commissaire/epreuves/${epreuveId}`,
      ],
      {
        headers: { Authorization: `Bearer ${token}` },
        tags: { endpoint: 'resultats_commissaire_epreuve' },
      },
      [200, 204, 403]
    );

    check(commissaireRes, {
      'resultats: commissaire endpoint responsive': (r) => r && (r.status === 200 || r.status === 204 || r.status === 403),
    });
  }
}

function runAbonnementCriticalFlow(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const res = http.get(`${abonnementBase}/api/abonnements/user/1`, {
    headers,
    tags: { endpoint: 'abonnement_user_list' },
  });

  check(res, {
    'abonnement: user subscriptions endpoint responsive': (r) => r.status === 200 || r.status === 204 || r.status === 401 || r.status === 403,
  });
}

function runBilletterieCriticalFlow(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const ticketsRes = http.get(`${billetterieBase}/api/tickets`, {
    headers,
    tags: { endpoint: 'billetterie_tickets_list' },
  });

  check(ticketsRes, {
    'billetterie: tickets endpoint responsive': (r) => r.status === 200 || r.status === 204 || r.status === 401 || r.status === 403,
  });

  const priceRes = http.get(`${billetterieBase}/api/tickets/price?category=STANDARD`, {
    headers,
    tags: { endpoint: 'billetterie_price' },
  });

  check(priceRes, {
    'billetterie: price endpoint responsive': (r) => r.status === 200 || r.status === 400 || r.status === 401 || r.status === 403,
  });
}

function runIncidentCriticalFlow(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const incidentsRes = http.get(`${incidentBase}/api/incidents`, {
    headers,
    tags: { endpoint: 'incident_list' },
  });

  check(incidentsRes, {
    'incident: list endpoint responsive': (r) => r.status === 200 || r.status === 204 || r.status === 401 || r.status === 403,
  });

  const debugRes = http.get(`${incidentBase}/api/debug/auth`, {
    headers,
    tags: { endpoint: 'incident_debug_auth' },
  });

  check(debugRes, {
    'incident: debug auth endpoint responsive': (r) => r.status === 200 || r.status === 401 || r.status === 403,
  });
}

function runLieuCriticalFlow() {
  const lieuxRes = http.get(`${lieuBase}/lieux`, {
    tags: { endpoint: 'lieu_list' },
  });

  check(lieuxRes, {
    'lieu: list endpoint reachable': (r) => r.status === 200 || r.status === 204,
  });
}

function runNotificationsCriticalFlow(token) {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const notifRes = http.get(`${notificationsBase}/api/notifications/spectateur/1`, {
    headers,
    tags: { endpoint: 'notifications_spectateur' },
  });

  check(notifRes, {
    'notifications: spectateur endpoint responsive': (r) => r.status === 200 || r.status === 204 || r.status === 401 || r.status === 403,
  });

  const counterRes = http.get(`${notificationsBase}/api/notifications/spectateur/1/compteur`, {
    headers,
    tags: { endpoint: 'notifications_compteur' },
  });

  check(counterRes, {
    'notifications: compteur endpoint responsive': (r) => r.status === 200 || r.status === 401 || r.status === 403,
  });
}

function runAnalyticsCriticalFlow(token) {
  if (!token) {
    return;
  }

  const headers = { Authorization: `Bearer ${token}` };
  const today = new Date().toISOString().slice(0, 10);

  const dailyRes = http.get(`${analyticsBase}/api/analytics/daily?date=${today}`, {
    headers,
    tags: { endpoint: 'analytics_daily' },
  });

  check(dailyRes, {
    'analytics: daily endpoint responsive': (r) => r.status === 200 || r.status === 404 || r.status === 401 || r.status === 403,
  });

  const eventsTodayRes = http.get(`${analyticsBase}/api/analytics/events/today`, {
    headers,
    tags: { endpoint: 'analytics_events_today' },
  });

  check(eventsTodayRes, {
    'analytics: events today endpoint responsive': (r) => r.status === 200 || r.status === 401 || r.status === 403,
  });
}

function runGeolocationCriticalFlow() {
  const fanzonesRes = http.get(`${geolocationBase}/api/geo/fanzones`, {
    tags: { endpoint: 'geo_fanzones' },
  });

  check(fanzonesRes, {
    'geolocation: fanzones endpoint reachable': (r) => r.status === 200 || r.status === 204,
  });

  const nearbyRes = http.get(`${geolocationBase}/api/geo/fanzones/nearby?lat=48.8566&lng=2.3522&rayon=1000`, {
    tags: { endpoint: 'geo_fanzones_nearby' },
  });

  check(nearbyRes, {
    'geolocation: nearby endpoint reachable': (r) => r.status === 200 || r.status === 204 || r.status === 400,
  });
}

function runVolunteerCriticalFlow(token) {
  if (!token) {
    return;
  }

  const headers = { Authorization: `Bearer ${token}` };
  const profileRes = http.get(`${volunteerBase}/api/v1/volunteers/profile`, {
    headers,
    tags: { endpoint: 'volunteer_profile' },
  });

  check(profileRes, {
    'volunteer: profile endpoint responsive': (r) => r.status === 200 || r.status === 404 || r.status === 401 || r.status === 403,
  });

  const todayRes = http.get(`${volunteerBase}/api/v1/volunteers/schedule/today`, {
    headers,
    tags: { endpoint: 'volunteer_schedule_today' },
  });

  check(todayRes, {
    'volunteer: schedule today endpoint responsive': (r) => r.status === 200 || r.status === 204 || r.status === 401 || r.status === 403,
  });
}

export default function () {
  group('critical_multi_service_paths', () => {
    const token = login();
    if (token) {
      getMe(token);
    }

    const epreuveId = discoverEpreuveId(token);

    group('event_service_critical_read_paths', () => {
      runEventCriticalFlow(token);
    });

    group('participants_service_critical_read_paths', () => {
      runParticipantsCriticalFlow(token);
    });

    group('resultats_service_critical_read_paths', () => {
      runResultatsCriticalFlow(token, epreuveId);
    });

    group('abonnement_service_critical_read_paths', () => {
      runAbonnementCriticalFlow(token);
    });

    group('billetterie_service_critical_read_paths', () => {
      runBilletterieCriticalFlow(token);
    });

    group('incident_service_critical_read_paths', () => {
      runIncidentCriticalFlow(token);
    });

    group('lieu_service_critical_read_paths', () => {
      runLieuCriticalFlow();
    });

    group('notifications_service_critical_read_paths', () => {
      runNotificationsCriticalFlow(token);
    });

    group('analytics_service_critical_read_paths', () => {
      runAnalyticsCriticalFlow(token);
    });

    group('geolocation_service_critical_read_paths', () => {
      runGeolocationCriticalFlow();
    });

    group('volunteer_service_critical_read_paths', () => {
      runVolunteerCriticalFlow(token);
    });
  });

  sleep(1);
}
