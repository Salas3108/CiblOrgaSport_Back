#!/bin/bash
# build-and-push.sh — Build toutes les images Docker et push sur Docker Hub
# Usage: ./deploy/build-and-push.sh
# Pré-requis: docker login effectué, DOCKERHUB_USERNAME défini dans .env.prod
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

if [ -f "$ROOT_DIR/.env.prod" ]; then
  export $(grep -v '^#' "$ROOT_DIR/.env.prod" | xargs)
fi

if [ -z "$DOCKERHUB_USERNAME" ]; then
  echo "ERREUR: DOCKERHUB_USERNAME non défini dans .env.prod"
  exit 1
fi

SERVICES=(
  "gateway:ciblorgasport-gateway"
  "auth-service:ciblorgasport-auth"
  "billetterie:ciblorgasport-billetterie"
  "event-service:ciblorgasport-event"
  "abonnement-service:ciblorgasport-abonnement"
  "incident-service:ciblorgasport-incident"
  "participants-service:ciblorgasport-participants"
  "resultats-service:ciblorgasport-resultats"
  "notifications-service:ciblorgasport-notifications"
  "lieu-service:ciblorgasport-lieu"
  "geolocation-service:ciblorgasport-geolocation"
  "analytics-service:ciblorgasport-analytics"
  "volunteer-service:ciblorgasport-volunteer"
)

echo "Build et push de ${#SERVICES[@]} images vers Docker Hub ($DOCKERHUB_USERNAME)..."
echo ""

FAILED=()
for entry in "${SERVICES[@]}"; do
  SERVICE_DIR="${entry%%:*}"
  IMAGE_NAME="${entry##*:}"
  FULL_IMAGE="$DOCKERHUB_USERNAME/$IMAGE_NAME:latest"

  echo "─── [$SERVICE_DIR] → $FULL_IMAGE"
  if docker build -t "$FULL_IMAGE" "$ROOT_DIR/$SERVICE_DIR"; then
    if docker push "$FULL_IMAGE"; then
      echo "    OK: $FULL_IMAGE"
    else
      echo "    ECHEC push: $FULL_IMAGE"
      FAILED+=("$SERVICE_DIR")
    fi
  else
    echo "    ECHEC build: $SERVICE_DIR"
    FAILED+=("$SERVICE_DIR")
  fi
  echo ""
done

if [ ${#FAILED[@]} -eq 0 ]; then
  echo "Toutes les images ont été buildées et poussées avec succès."
else
  echo "ECHECS (${#FAILED[@]}) :"
  for f in "${FAILED[@]}"; do echo "  - $f"; done
  exit 1
fi
