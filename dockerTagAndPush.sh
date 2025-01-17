#!/bin/bash

# 서비스 리스트
services=(
  "eureka" "gateway" "payment" "queue"
  "reservation" "review" "store" "user"
)

# 이미지 이름
imageName="laira2/quit"

# 각 서비스에 대해 이미지 빌드 및 푸시
for service in "${services[@]}"
do
  latestTag="$service-latest"  # latest 태그
  echo "=======현재 위치 : $(pwd) ========="
  # 도커 이미지 빌드 (각 서비스 디렉토리에 Dockerfile이 있어야 함)
  docker build -t "$imageName:$latestTag" "/home/runner/work/quit/quit/$service"

  # Docker Hub에 푸시
  docker push "$imageName:$latestTag"

  echo "$service 이미지가 빌드되어 Docker Hub에 $imageName:$latestTag 태그로 푸쉬되었습니다."
done

echo "모든 서비스의 이미지 빌드 및 푸쉬가 완료되었습니다."
