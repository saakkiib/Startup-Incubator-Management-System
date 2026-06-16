#!/bin/bash
export PATH="/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:$HOME/.sdkman/candidates/java/current/bin:/usr/libexec/java_home/bin"
PROJECT="/Applications/XAMPP/xamppfiles/htdocs/Startup-Incubator-Management-System-main"
JAR="$PROJECT/backend/target/management-0.0.1-SNAPSHOT.jar"
lsof -ti :8085 | xargs kill -9 2>/dev/null
sleep 1
cd "$PROJECT/backend"
nohup java -jar "$JAR" > /tmp/incubator-backend.log 2>&1 &
sleep 4
open "http://localhost/Startup-Incubator-Management-System-main/index.html"
echo ""
echo "========================================"
echo " ✅ Backend started on port 8085"
echo "    http://localhost:8085"
echo " 📄 Log: tail -f /tmp/incubator-backend.log"
echo "========================================"
