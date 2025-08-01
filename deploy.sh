#!/bin/bash

# EC2 정보 설정
EC2_USER_SERVER_1=ubuntu
EC2_IP_SERVER_1=43.200.182.67
EC2_USER_SERVER_2=ubuntu
EC2_IP_SERVER_2=13.124.121.62
PEM_PATH=~/keys/msa-0508.pem
REMOTE_DIR=/home/ubuntu

# 1. 로컬 빌드
echo "> 로컬에서 Gradle 빌드 시작"
./gradlew clean bootJar -x test

# 2. 빌드된 JAR 경로 찾기
BUILD_PATH=$(find build/libs -name "*.jar" | head -n 1)
JAR_NAME=$(basename "$BUILD_PATH")
echo "> 빌드 완료: $JAR_NAME"

# 3. EC2로 JAR 전송
echo "> EC2로 JAR 파일 전송 중"
scp -i "$PEM_PATH" -o StrictHostKeyChecking=accept-new \
  "$BUILD_PATH" "$EC2_USER_SERVER_1@$EC2_IP_SERVER_1:$REMOTE_DIR/$JAR_NAME" \
  && echo "✅ 전송 성공" || { echo "❌ 전송 실패"; exit 1; }
# 4. EC2에서 실행 중인 프로세스 종료 후 재실행
echo "> EC2에서 애플리케이션 재시작 중"
ssh -i "$PEM_PATH" "$EC2_USER_SERVER_1@$EC2_IP_SERVER_1" <<EOF
  echo "> 현재 실행 중인 프로세스 확인"
  PID=\$(pgrep -f $JAR_NAME)
  if [ -z "\$PID" ]; then
    echo "> 실행 중인 애플리케이션 없음"
  else
    echo "> PID: \$PID 종료"
    kill -15 \$PID
    sleep 3
  fi
  echo "> 새로운 애플리케이션 실행"
  nohup java -jar "$JAR_NAME" --spring.profiles.active=dev >> app.log 2>&1 < /dev/null &
EOF

# 3. EC2로 JAR 전송
echo "> EC2로 JAR 파일 전송 중"
scp -i "$PEM_PATH" -o StrictHostKeyChecking=accept-new \
  "$BUILD_PATH" "$EC2_USER_SERVER_2@$EC2_IP_SERVER_2:$REMOTE_DIR/$JAR_NAME" \
  && echo "✅ 전송 성공" || { echo "❌ 전송 실패"; exit 1; }
# 4. EC2에서 실행 중인 프로세스 종료 후 재실행
echo "> EC2에서 애플리케이션 재시작 중"
ssh -i "$PEM_PATH" "$EC2_USER_SERVER_2@$EC2_IP_SERVER_2" <<EOF
  echo "> 현재 실행 중인 프로세스 확인"
  PID=\$(pgrep -f $JAR_NAME)
  if [ -z "\$PID" ]; then
    echo "> 실행 중인 애플리케이션 없음"
  else
    echo "> PID: \$PID 종료"
    kill -15 \$PID
    sleep 3
  fi
  echo "> 새로운 애플리케이션 실행"
  nohup java -jar "$JAR_NAME" --spring.profiles.active=dev >> app.log 2>&1 < /dev/null &
EOF

echo "> 배포 완료"
