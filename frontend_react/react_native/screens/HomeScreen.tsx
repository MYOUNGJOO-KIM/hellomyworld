// screens/HomeScreen.tsx
import React from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';
//import LinearGradient from 'react-native-linear-gradient';

interface Props {
  navigation: any; // 더 정교한 타입으로 변경할 수 있습니다.
}

const HomeScreen: React.FC<Props> = ({ navigation }) => {

  // 현재 날짜를 기반으로 이번 달의 일자 수 계산
  const today = new Date();
  const currentMonth = today.getMonth(); // 0부터 시작하는 월
  const currentYear = today.getFullYear();
  const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
  const weeks = Math.ceil(daysInMonth / 7); // 주 수 계산


  return (
    // <LinearGradient
    //   colors={['#432d51', '#8963a1']} // 그라데이션 색상 설정
    //   style={styles.container}
    // >
      
    // </LinearGradient>
    <View style={styles.container}>
        {Array.from({ length: weeks }, (_, weekIndex) => (
          <View key={weekIndex} style={styles.row}>
            {Array.from({ length: 7 }, (_, dayIndex) => {
              const day = weekIndex * 7 + dayIndex + 1;
              return (
                <View key={day} style={styles.box}>
                  <Text style={styles.boxText}>{day <= daysInMonth ? day : ''}</Text>
                </View>
              );
            })}
          </View>
        ))}
      </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 40,
    backgroundColor: '#F5FCFF',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  box: {
    width: '13%', // 7개 열의 경우 각 박스 너비 조정
    height: 50,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ccc',
    backgroundColor: '#fff',
  },
  boxText: {
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default HomeScreen;
