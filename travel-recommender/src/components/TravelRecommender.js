import React, { useState, useEffect } from 'react';
//import GoogleMapsApi from './GoogleMapsApi';
import LeafletApi from './LeafletApi';

// 임의 좌표 생성 함수
function getRandomCoordinate(min, max) {
  return Math.random() * (max - min) + min;
}

function TravelRecommender() {
  const [startLatLng, setStartLatLng] = useState({lat : '37.5665', lng : '126.9780'});//서울로 초기화
  const [endLatLng, setEndLatLng] = useState({lat : '37.5665', lng : '126.9780'});//서울로 초기화
  const [focusLatLng, setFocusLatLng] = useState({lat : '37.5665', lng : '126.9780'});
  const [region, setRegion] = useState("전체");
  //const [map, setMap] = useState(null);

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

  const randomCoordinates = () => {
    const { lat, lng } = getCoordinatesForRegion(region);
    setEndLatLng({ lat : lat.toFixed(4), lng : lng.toFixed(4) });
    setFocusLatLng({ lat : ((Number(endLatLng.lat) + Number(startLatLng.lat))/2).toFixed(4), lng : ((Number(endLatLng.lng) + Number(startLatLng.lng))/2).toFixed(4)});
  }

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

      <button onClick={randomCoordinates}>여행지 추천 받기</button>
      
      {endLatLng && (
        <div>
          <h2>추천 좌표:</h2>
          <p>위도: {endLatLng.lat}</p>
          <p>경도: {endLatLng.lng}</p>
          <LeafletApi startLatLng={startLatLng} endLatLng={endLatLng} focusLatLng ={focusLatLng} />
        </div>
      )}
    </div>
  );
}
export default TravelRecommender;

