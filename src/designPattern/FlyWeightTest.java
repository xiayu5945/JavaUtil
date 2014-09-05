package designPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 FlyWeightFactory负责创建和管理享元单元，当一个客户端请求时，工厂需要检查当前对象池中是否有符合条件的对象，如果有，就返回已经存在的对象，如果没有，则创建一个新对象
 */
public class FlyWeightTest {
	
	public static void main(String[] args) {
		System.out.println(PoolMap.getInstance().getFlyWeightObject("a"));
		System.out.println(PoolMap.getInstance().getFlyWeightObject("b"));
		System.out.println(PoolMap.getInstance().getFlyWeightObject("c"));
		System.out.println(PoolMap.getInstance().getFlyWeightObject("a"));
	}

}
class Pool{
	private static final int POOLSIZE = 10;
	private static Pool instance = new Pool();
	private List<FlyWeightObject> list = new ArrayList<FlyWeightObject>();
	private Pool(){
		for(int i=0;i<POOLSIZE;i++){
			list.add(new FlyWeightObject(i));
		}
	}
	
	public FlyWeightObject getFlyWeightObject(int i){
		if(i<0 || i>=POOLSIZE )return null;
		return list.get(i);
	}
	public static Pool getInstance(){
		return instance;
	}
}
class FlyWeightObject{
	private int i =0;
	public FlyWeightObject(int i) {
		this.i = i;
	}
	public String toString(){
		return i+"";
	}
	
}

class PoolMap{
	private Map<String, FlyWeightObject> map = new HashMap<String, FlyWeightObject>();
	private static PoolMap instance = new PoolMap();
	public static PoolMap getInstance(){
		return instance;
	}
	public FlyWeightObject getFlyWeightObject(String objName){
		FlyWeightObject obj = map.get(objName);
		if(obj != null) return obj;
		obj = new FlyWeightObject((int)(Math.random()*100));
		map.put(objName, obj);
		return obj;
	}
}