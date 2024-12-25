import React, { useState, useEffect } from 'react';
import L from 'leaflet';  // Leaflet 라이브러리 임포트
import 'leaflet/dist/leaflet.css';  // Leaflet CSS
import 'leaflet-routing-machine';  // Leaflet Routing Machine 플러그인

// 임의 좌표 생성 함수
function getRandomCoordinate(min, max) {
  return Math.random() * (max - min) + min;
}

function TravelRecommender() {
  const [destination, setDestination] = useState(null);
  const [region, setRegion] = useState("전체");
  const [map, setMap] = useState(null);

  // 기본 마커와 그림자 비활성화
  useEffect(() => {
    L.Icon.Default.prototype._getIconUrl = function () {
      return null;  // 기본 아이콘 URL 비활성화
    };
  }, []);

  // 커스텀 마커 아이콘 설정
  const customIcon = L.icon({
    iconUrl: 'https://cdn-icons-png.flaticon.com/512/3448/3448366.png',  // 커스텀 아이콘 URL
    iconSize: [25, 41],  // 아이콘 크기
    iconAnchor: [12, 41],  // 아이콘 앵커 설정 (기본적으로 아래쪽)
    popupAnchor: [1, -34],  // 팝업 위치 설정
    shadowUrl: null,  // 그림자 이미지 비활성화
    iconRetinaUrl: null,  // 2배 해상도 마커 이미지 비활성화
    shadowSize: [0, 0],  // 그림자 크기 0으로 설정
    shadowAnchor: [0, 0]  // 그림자 앵커도 비활성화
  });

  // 지역 선택 시 좌표 범위 설정
  const getCoordinatesForRegion = (region) => {
    switch(region) {
      case "경기도":
        return {
          lat: getRandomCoordinate(37.0, 38.1),  // 경기도 내륙
          lng: getRandomCoordinate(126.6, 127.6)  // 경기도 내륙
        };
      case "서울특별시":
        return {
          lat: getRandomCoordinate(37.4, 37.7),  // 서울 범위
          lng: getRandomCoordinate(126.7, 127.1)  // 서울 범위
        };
      case "인천광역시":
        return {
          lat: getRandomCoordinate(37.3, 37.7),  // 인천 범위
          lng: getRandomCoordinate(126.4, 126.8)  // 인천 범위
        };
      case "전체":
      default:
        return {
          lat: getRandomCoordinate(37.0, 38.1),  // 남한 전역 (북한 제외)
          lng: getRandomCoordinate(126.6, 127.6)  // 남한 전역 (북한 제외)
        };
    }
  };

  const recommendPlace = () => {
    const { lat, lng } = getCoordinatesForRegion(region);
    setDestination({ lat, lng });

    // 지도 생성 (Leaflet.js 사용)
    if (map) {
      map.setView([lat, lng], 18);  // 줌 레벨을 18로 설정하여 더 가까운 범위로 보이게 함

      // 출발지 마커 추가 (서울)
      const startLatLng = [37.5665, 126.9780];  // 서울 시청 좌표 (출발지)

      // 도착지 마커 추가 (추천된 랜덤 좌표)
      const endLatLng = [lat, lng];  // 랜덤 여행지 좌표 (도착지)

      // 길찾기 경로 추가
      L.Routing.control({
        waypoints: [
          L.latLng(startLatLng[0], startLatLng[1]),  // 출발지
          L.latLng(endLatLng[0], endLatLng[1])  // 도착지
        ],
        routeWhileDragging: true,  // 경로를 드래그할 때마다 업데이트
      }).addTo(map);
    } else {
      const newMap = L.map('map').setView([37.5665, 126.9780], 14);  // 서울로 기본 설정

      // OpenStreetMap Tile Layer 추가
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(newMap);

      // 위성 이미지 Tile Layer 추가 (ESRI 또는 다른 타일 제공자 사용)
      L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
        attribution: '&copy; <a href="https://www.esri.com/en-us/home">Esri</a> contributors'
      }).addTo(newMap);

      // 출발지 마커 추가 (서울)
      const startLatLng = [37.5665, 126.9780];  // 서울 시청 좌표 (출발지)

      // 도착지 마커 추가 (추천된 랜덤 좌표)
      const endLatLng = [lat, lng];  // 랜덤 여행지 좌표 (도착지)

      // 길찾기 경로 추가
      L.Routing.control({
        waypoints: [
          L.latLng(startLatLng[0], startLatLng[1]),  // 출발지
          L.latLng(endLatLng[0], endLatLng[1])  // 도착지
        ],
        routeWhileDragging: true,  // 경로를 드래그할 때마다 업데이트
      }).addTo(newMap);

      setMap(newMap);
    }
  };

  return (
    <div>
      <h1>랜덤 여행지 추천</h1>
      
      {/* 지역 선택 콤보박스 */}
      <select onChange={(e) => setRegion(e.target.value)} value={region}>
        <option value="전체">전체 (South Korea)</option>
        <option value="경기도">경기도</option>
        <option value="서울특별시">서울특별시</option>
        <option value="인천광역시">인천광역시</option>
      </select>

      <button onClick={recommendPlace}>여행지 추천 받기</button>
      
      {destination && (
        <div>
          <h2>추천 좌표:</h2>
          <p>위도: {destination.lat.toFixed(4)}</p>
          <p>경도: {destination.lng.toFixed(4)}</p>
        </div>
      )}

      {/* Leaflet 지도 표시 영역 */}
      <div id="map" style={{ width: "100%", height: "400px" }}></div>
    </div>
  );
}

export default TravelRecommender;
