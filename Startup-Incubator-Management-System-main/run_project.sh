#!/bin/bash

echo "🚀 Starting IncubatorX Management System..."

# Navigate to backend and start Spring Boot
echo "📦 Starting Backend (Spring Boot)..."
cd backend
if command -v mvn &> /dev/null
then
    # Start in background
    mvn spring-boot:run &
    echo "✅ Backend starting on http://localhost:8085..."
else
    echo "❌ Error: Maven (mvn) is not installed. Please run the backend from your IDE (IntelliJ/VS Code)."
    exit 1
fi

# Wait a bit for backend to initialize
sleep 5

# Open Frontend
echo "🌐 Opening Frontend..."
cd ..
if [[ "$OSTYPE" == "darwin"* ]]; then
    open index.html
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    xdg-open index.html
else
    start index.html
fi

echo "✨ Everything is set! Check your browser."
