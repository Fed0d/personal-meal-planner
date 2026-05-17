#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
KEYS_DIR="$PROJECT_ROOT/keys"

echo "Generating RSA 2048-bit key pair..."
mkdir -p "$KEYS_DIR"

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 \
  -out "$KEYS_DIR/private_key.pem" 2>/dev/null
openssl rsa -pubout \
  -in "$KEYS_DIR/private_key.pem" \
  -out "$KEYS_DIR/public_key.pem" 2>/dev/null

chmod 600 "$KEYS_DIR/private_key.pem"
chmod 644 "$KEYS_DIR/public_key.pem"

echo "Keys written to $KEYS_DIR"
echo ""
echo "Copying into service resources for local development..."

SERVICES_NEEDING_PUBLIC_KEY=(
  "auth-service"
  "user-service"
  "meal-service"
  "meal-plan-service"
  "meal-plan-orchestrator"
)

for svc in "${SERVICES_NEEDING_PUBLIC_KEY[@]}"; do
  TARGET_DIR="$PROJECT_ROOT/$svc/src/main/resources/keys"
  mkdir -p "$TARGET_DIR"
  cp "$KEYS_DIR/public_key.pem" "$TARGET_DIR/public_key.pem"
  echo "  -> $svc/src/main/resources/keys/public_key.pem"
done

AUTH_KEYS_DIR="$PROJECT_ROOT/auth-service/src/main/resources/keys"
mkdir -p "$AUTH_KEYS_DIR"
cp "$KEYS_DIR/private_key.pem" "$AUTH_KEYS_DIR/private_key.pem"
echo "  -> auth-service/src/main/resources/keys/private_key.pem"

echo ""
echo "Done."
echo "  Docker deployment : keys are mounted from ./keys/"
echo "  Local development : keys are in each service's src/main/resources/keys/"
