#!/usr/bin/env bash
set -euo pipefail

# Run Maven tests and save output into log/ with a timestamped filename.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${REPO_ROOT}"
TS="$(date +%Y%m%d-%H%M%S)"
OUTFILE="${REPO_ROOT}/log/mvn-test-${TS}.log"

mvn test | tee "${OUTFILE}"
echo "Saved test output to ${OUTFILE}"
