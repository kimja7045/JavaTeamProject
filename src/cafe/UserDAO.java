package cafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class UserDAO {
	// --------------------------------------------------------
	// 싱글톤 디자인 패턴을 적용한 인스턴스 리턴
	private static UserDAO instance = new UserDAO();
	
	private UserDAO() {}
	
	public static UserDAO getInstance() {
		return instance;
	}
	// --------------------------------------------------------
	
	
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;

	private void connectDb() {
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://mydb.cihjn38frpag.ap-northeast-2.rds.amazonaws.com:3305/team4";
		String user = "mydb";
		String password = "dkagh1212";
		
		try {
			Class.forName(driver);
//			System.out.println("드라이버 로드 성공!");
			
			con = DriverManager.getConnection(url, user, password);
//			System.out.println("DB 접속 성공!");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로드 실패! - " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("DB 접속 실패! - " + e.getMessage());
		}
	}
	
	private void closeDb() {
		if(rs != null) try { rs.close(); } catch(Exception e) {}
		if(pstmt != null) try { pstmt.close(); } catch(Exception e) {}
		if(con != null) try { con.close(); } catch(Exception e) {} 
	}
	
	// 회원 추가
	public int insert(UserDTO dto) {
		connectDb();
		
		int result = 0; // 회원 추가 성공 여부(0 : 실패, 1 : 리턴)
		
		try {
			// DTO 객체에 저장된 데이터를 DB 에 INSERT
			String sql = "INSERT INTO User VALUES (null,?,?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, dto.getpNum());
			pstmt.setDouble(2, dto.getPoint());
			
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - " + e.getMessage());
		} finally {
			closeDb();
		}
		
		return result;
	}
	// 회원 수정
		public int update(UserDTO dto) {
			connectDb();

			int result = 0; // 회원 수정 성공 여부(0 : 실패, 1 : 리턴)

			try {
				// 레코드 수정
				String sql = "update user set " + "pNum=?, " + "point=?" + " where idx=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, dto.getpNum());
				pstmt.setInt(2, dto.getPoint());
				pstmt.setInt(3, dto.getIdx());
				pstmt.executeUpdate();
				result = pstmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println("SQL 구문 오류! - " + e.getMessage());
			} finally {
				closeDb();
			}

			return result;
		}
	
	// 회원 삭제
	public int delete(int idx) {
		connectDb();
		
		int result = 0; // 회원 삭제 성공 여부(0 : 실패, 1 : 리턴)
		
		try {
			// 전달받은 번호(idx)를 사용하여 레코드 삭제
			String sql = "DELETE FROM User WHERE idx=?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, idx);
			
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - " + e.getMessage());
		} finally {
			closeDb();
		}
		
		return result;
	}

	
	// 회원목록 조회
	public Vector<Vector> select() {
		connectDb();
		
		try {
			String sql = "SELECT * FROM user";
			
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			Vector<Vector> data = new Vector<>(); // 전체 레코드를 저장할 Vector 객체
			
			while(rs.next()) {
				Vector rowData = new Vector<>(); // 1개 레코드를 저장할 Vector 객체
				
				rowData.add(rs.getInt("idx"));
				rowData.add(rs.getString("pNum"));
				rowData.add(rs.getString("point"));
				
				data.add(rowData);
			}
			
			return data; // 조회 성공 시 저장된 Vector 객체 리턴

		} catch (SQLException e) {
			System.out.println("SQL 구문 오류! - " + e.getMessage());
		} finally {
			closeDb();
		}
		
		return null;
	}
}