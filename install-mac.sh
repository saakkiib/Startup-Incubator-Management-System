#!/bin/bash
# ============================================================
# install-mac.sh — One-time setup for Mac
# Run this ONCE: bash install-mac.sh
# After this, everything starts automatically.
# ============================================================

set -e
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
ok()   { echo -e "${GREEN}✅ $1${NC}"; }
warn() { echo -e "${YELLOW}⚠️  $1${NC}"; }
info() { echo -e "🔧 $1"; }

PROJECT_DIR="/Applications/XAMPP/xamppfiles/htdocs/Startup-Incubator-Management-System-main"
BACKEND_DIR="$PROJECT_DIR/backend"
JAR_PATH="$BACKEND_DIR/target/management-0.0.1-SNAPSHOT.jar"
PLIST_PATH="$HOME/Library/LaunchAgents/com.incubator.backend.plist"

echo ""
echo "================================================"
echo "   IncubatorX — Mac Auto Setup"
echo "================================================"
echo ""

# ── 1. Homebrew ──────────────────────────────────────────────────────────────
info "Checking Homebrew..."
if ! command -v brew &>/dev/null; then
    warn "Homebrew not found. Installing..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    ok "Homebrew installed."
else
    ok "Homebrew already installed."
fi

# ── 2. Java 17 ───────────────────────────────────────────────────────────────
info "Checking Java..."
if ! command -v java &>/dev/null; then
    warn "Java not found. Installing Java 17..."
    brew install openjdk@17
    echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
    export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
    ok "Java 17 installed."
else
    ok "Java already installed: $(java -version 2>&1 | head -1)"
fi

# ── 3. Maven ─────────────────────────────────────────────────────────────────
info "Checking Maven..."
if ! command -v mvn &>/dev/null; then
    warn "Maven not found. Installing..."
    brew install maven
    ok "Maven installed."
else
    ok "Maven already installed."
fi

# ── 4. XAMPP check ───────────────────────────────────────────────────────────
info "Checking XAMPP..."
if [ ! -f "/Applications/XAMPP/xamppfiles/bin/mysql" ]; then
    warn "XAMPP not found!"
    echo ""
    echo "Please download and install XAMPP from:"
    echo "👉 https://www.apachefriends.org/download.html"
    echo "Then re-run this script."
    exit 1
else
    ok "XAMPP found."
fi

# ── 5. Build JAR ─────────────────────────────────────────────────────────────
info "Building Spring Boot backend..."
cd "$BACKEND_DIR"
mvn package -DskipTests -q
ok "Backend JAR built: $JAR_PATH"

# ── 6. Setup database ────────────────────────────────────────────────────────
info "Setting up database..."
/Applications/XAMPP/xamppfiles/bin/mysql -u root -e "CREATE DATABASE IF NOT EXISTS incubator_db;" 2>/dev/null || true
ok "Database ready."

# ── 7. LaunchAgent (auto-start on login) ─────────────────────────────────────
info "Setting up auto-start on login..."
JAVA_BIN=$(which java)

cat > "$PLIST_PATH" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>com.incubator.backend</string>
    <key>ProgramArguments</key>
    <array>
        <string>$JAVA_BIN</string>
        <string>-jar</string>
        <string>$JAR_PATH</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>KeepAlive</key>
    <true/>
    <key>StandardOutPath</key>
    <string>/tmp/incubator-backend.log</string>
    <key>StandardErrorPath</key>
    <string>/tmp/incubator-backend-error.log</string>
</dict>
</plist>
EOF

# Unload if already loaded
launchctl unload "$PLIST_PATH" 2>/dev/null || true
launchctl load "$PLIST_PATH"
ok "Auto-start configured (runs on every login)."

echo ""
echo "================================================"
echo "   ✅ Setup Complete!"
echo "================================================"
echo ""
echo "🌐 Open your browser and go to:"
echo "   http://localhost/Startup-Incubator-Management-System-main/index.html"
echo ""
echo "Everything will start automatically from now on."
echo ""
