# 라즈베리파이에서 실행할 Python 코드
import socket

def start_server():
    # 소켓 객체를 생성
    server_socket = socket.socket(socket.AF_INET[1], socket.SOCK_STREAM)
    # 호스트와 포트 설정
    host = '0.0.0.0'  # 모든 인터페이스에서 연결을 허용
    port = 12345  # 사용할 포트 번호

    # 소켓을 포트에 바인딩
    server_socket.bind((host, port))
    # 클라이언트의 연결을 기다림
    server_socket.listen(1)
    print("서버가 시작되었습니다. 연결을 기다리는 중...")

    while True:
        # 클라이언트 연결 수락
        client_socket, addr = server_socket.accept()
        print(f"연결 수락됨: {addr}")

        # 클라이언트로부터 데이터 수신
        data = client_socket.recv(1024).decode()
        print(f"받은 데이터: {data}")

        # 클라이언트에 응답
        response = "Hello from Raspberry Pi"
        client_socket.send(response.encode())

        # 소켓 닫기
        client_socket.close()

if __name__ == "__main__":
    start_server()