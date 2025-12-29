#!/usr/bin/env bash
set -e

HOST="$1"
PORT="$2"
shift 2

echo "Waiting for $HOST:$PORT..."
until nc -z "$HOST" "$PORT"; do
  sleep 1
done
echo "$HOST:$PORT is up"

# If arguments remain, execute them
if [ "$1" = "--" ]; then
  shift
fi

exec "$@"
