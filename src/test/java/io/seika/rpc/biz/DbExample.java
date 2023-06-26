package io.seika.rpc.biz;

import java.util.List;

import io.seika.rpc.biz.model.HelpTopic;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;


public class DbExample {  
	
	@Autowired
	SqlSession sqlSession;
	
	public List<HelpTopic> test(){
		return sqlSession.selectList("io.zbus.rpc.biz.db.test"); 
	}
}
