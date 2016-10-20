package org.com;



import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

//应用工具类
public class JsonUtil {
	/**
     * 实现将实体对象转换成json对象
     * @param medicine    Medicine对象
     * @return
     */
	
	public static String obj2JsonData(Medicine medicine)
	{
		String jsonData = null;
		try {
			//使用obj2JsonData创建json数据
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("id",medicine.getId())
			.field("name",medicine.getName())
			.field("function",medicine.getFunction())
			.endObject();
			
			jsonData = jsonBuild.string();
			//System.out.println(jsonData);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return jsonData;
	}
	
}
