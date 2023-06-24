
package io.zbus.nacos.properties;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.utils.NetUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import io.zbus.config.SeikaProperties;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class Register extends Instance {
	private String groupName = Constants.DEFAULT_GROUP;

	public String getGroupName() {
		return groupName;
	}
	public static String WrapServiceName(String name){
		return "seika-rpc."+name;
}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

 	public  Register(SeikaProperties properties,String appName){
		if (StringUtils.isEmpty(appName)){
			throw new RuntimeException("serviceName notNull");
		}
		this.setServiceName(WrapServiceName(appName));
		if(properties!=null){
			this.setPort(properties.getRpcPort());
		}
		this.setInstanceId("");

		this.setIp(NetUtils.localIP());

 	}
}
