import org.apache.ibatis.session.SqlSession;

public class findDao {
    public courses findByName(String name)
    {
        SqlSession sqlSession=util.getSqlSession();
        try{

            return sqlSession.selectOne("EmployeeMapper.findName",name);
            }catch (Exception e)
            {
                e.printStackTrace();
                sqlSession.rollback();
            }finally {
                util.closeSqlSession();
            }
        return null;
    }
    public static void main(String[] args)
    {
            findDao dao=new findDao();
            courses course=dao.findByName("android");
            System.out.println(course.getId()+"   "+course.getCname());
    }
}
