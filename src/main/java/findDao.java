import org.apache.ibatis.session.SqlSession;

public class findDao {
    public courses findByName(String name)
    {
        SqlSession currentSession = util.getSqlSession();
        try{

            return currentSession.selectOne("EmployeeMapper.findName",name);
            }catch (org.apache.ibatis.exceptions.PersistenceException e)
            {
                e.printStackTrace();
                if (currentSession != null) {
                    currentSession.rollback();
                }
            }finally {
                util.closeSqlSession();
            }
        return null;
    }
    public static void main(String[] args)
    {
            findDao dao=new findDao();
            courses course=dao.findByName("android");
            if (course == null) {
                System.out.println("Course not found.");
            } else {
                System.out.println(course.getId()+"   "+course.getCname());
            }
    }
}
