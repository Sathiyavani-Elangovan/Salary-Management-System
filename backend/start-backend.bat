@echo off
cd /d "%~dp0"
java -Dmicronaut.server.port=8080 -Dmicronaut.environments=dev -Dlogback.configurationFile=classpath:logback.xml -Dlogger.levels.root=INFO -Dlogger.levels.io.micronaut=INFO -jar build\libs\salary-management-backend-0.1-all.jar 2>&1
