package org.com;



import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

//Ӧ�ù�����
public class JsonUtil {
	/**
     * ʵ�ֽ�ʵ�����ת����json����
     * @param medicine    Medicine����
     * @return
     */
	
	public static String obj2JsonData(Medicine medicine)
	{
		String jsonData = null;
		try {
			//ʹ��obj2JsonData����json����
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
