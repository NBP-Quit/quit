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
  echo "======= 현재 위치 : $(pwd) ======="
  echo "======= 확인 중인 서비스 : $service ======="

 # 디렉토리와 파일 확인
  servicePath="/home/runner/work/quit/quit/$service"
  echo "=== 디렉토리 내용 확인: $servicePath ==="
  if [ -d "$servicePath" ]; then
    ls -l "$servicePath"
  else
    echo "$servicePath 디렉토리가 존재하지 않습니다."
  fi

  # eureka 디렉토리 검색
  if [ "$service" == "eureka" ]; then
    echo "=== 'eureka' 디렉토리 위치 검색 ==="
    find / -type d -name "eureka" 2>/dev/null
  fi

  # 도커 이미지 빌드 (각 서비스 디렉토리에 Dockerfile이 있어야 함)
  if [ -d "$servicePath" ]; then
    docker build -t "$imageName:$latestTag" "$servicePath"
  else
    echo "$servicePath 경로가 잘못되었거나 디렉토리가 없습니다."
  fi

  # Docker Hub에 푸시
  docker push "$imageName:$latestTag"

  echo "$service 이미지가 빌드되어 Docker Hub에 $imageName:$latestTag 태그로 푸쉬되었습니다."
done

echo "모든 서비스의 이미지 빌드 및 푸쉬가 완료되었습니다."
