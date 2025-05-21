import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.ibatis.session.SqlSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify; // Added for rollback verification
import static org.mockito.Mockito.doNothing; // Added for mocking void methods

@ExtendWith(MockitoExtension.class)
class findDaoTest {

    @Mock
    private SqlSession mockSqlSession;

    private findDao dao;

    @BeforeEach
    void setUp() {
        dao = new findDao();
    }

    @Test
    void findByName_existingCourse() {
        try (MockedStatic<util> mockedUtil = Mockito.mockStatic(util.class)) {
            mockedUtil.when(util::getSqlSession).thenReturn(mockSqlSession);
            mockedUtil.when(util::closeSqlSession).doesNothing(); // Ensure it can be called

            courses expectedCourse = new courses();
            // Assuming courses has setId and setCname or a constructor
            // For now, we will rely on default constructor and direct field access if public,
            // or assume setters exist as per standard Java bean practices.
            // Let's assume setters:
            expectedCourse.setId(1);
            expectedCourse.setCname("Test Course");

            when(mockSqlSession.selectOne(eq("EmployeeMapper.findName"), eq("existingCourse"))).thenReturn(expectedCourse);

            courses actualCourse = dao.findByName("existingCourse");

            assertNotNull(actualCourse);
            assertEquals(expectedCourse.getId(), actualCourse.getId());
            assertEquals(expectedCourse.getCname(), actualCourse.getCname());
            
            // Verify closeSqlSession was called via the static mock context
            mockedUtil.verify(util::closeSqlSession);
        }
    }

    @Test
    void findByName_nonExistingCourse() {
        try (MockedStatic<util> mockedUtil = Mockito.mockStatic(util.class)) {
            mockedUtil.when(util::getSqlSession).thenReturn(mockSqlSession);
            mockedUtil.when(util::closeSqlSession).doesNothing(); // Ensure it can be called

            when(mockSqlSession.selectOne(eq("EmployeeMapper.findName"), eq("nonExistingCourse"))).thenReturn(null);

            courses actualCourse = dao.findByName("nonExistingCourse");

            assertNull(actualCourse);
            
            // Verify closeSqlSession was called
            mockedUtil.verify(util::closeSqlSession);
        }
    }
    
    @Test
    void findByName_exceptionDuringQuery() {
        try (MockedStatic<util> mockedUtil = Mockito.mockStatic(util.class)) {
            mockedUtil.when(util::getSqlSession).thenReturn(mockSqlSession);
            mockedUtil.when(util::closeSqlSession).doesNothing(); // Ensure it can be called

            // Simulate an exception during selectOne
            when(mockSqlSession.selectOne(eq("EmployeeMapper.findName"), eq("faultyCourse"))).thenThrow(new RuntimeException("Database error"));
            
            // Mock the rollback method on mockSqlSession
            doNothing().when(mockSqlSession).rollback();

            courses actualCourse = dao.findByName("faultyCourse");

            assertNull(actualCourse); // Expect null as per current findDao.java logic
            
            // Verify rollback was called on mockSqlSession
            verify(mockSqlSession).rollback();
            
            // Verify closeSqlSession was called
            mockedUtil.verify(util::closeSqlSession);
        }
    }
}
