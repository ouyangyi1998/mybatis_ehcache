import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

public class util {

    private  static SqlSessionFactory sqlSessionFactory;
    private  static ThreadLocal<SqlSession> threadLocal=new ThreadLocal<SqlSession>();

    static {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public  static  SqlSession getSqlSession()
    {
       SqlSession  sqlSession=threadLocal.get();
       if(sqlSession==null)
       {
           sqlSession=sqlSessionFactory.openSession();
            threadLocal.set(sqlSession);
       }
       return sqlSession;
    }
    public static void closeSqlSession()
    {
          SqlSession sqlSession=threadLocal.get();
          if(sqlSession!=null)
          {
              sqlSession.close();
              threadLocal.remove();
          }
    }
    public static void main(String[] args)
    {
        Connection connection=util.getSqlSession().getConnection();
        System.out.print(connection!=null?"true":"false");
    }

}
