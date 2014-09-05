package designPattern;

/**
 * 应用场景：扩展类Target 的方法 
 * 类的适配器模式：当希望将一个类转换成满足另一个新接口的类时，可以使用类的适配器模式，创建一个新类，继承原有的类，实现新的接口即可。
 * 对象的适配器模式：当希望将一个对象转换成满足另一个新接口的对象时，可以创建一个Wrapper类，持有原类的一个实例，在Wrapper类的方法中，调用实例的方法就行。
 * 接口的适配器模式：当不希望实现一个接口中所有的方法时，可以创建一个抽象类Wrapper，实现所有方法，我们写别的类的时候，继承抽象类即可。
 */

public class AdapterTest {
	public static void main(String[] args) {
		ISource source = new Adapter();
		source.method1();
		source.method2();
		
		ISource source1 = new AdapterInterface1();
		ISource source2 = new AdapterInterface2();
		source1.method1();
		source2.method2();
	}
}


class Target{
	public void method1(){
		System.out.println("Target:method1");
	}
}
interface ISource {
	public void method1();
	public void method2();
}

//类的适配器模式
class Adapter extends Target implements ISource{

	@Override
	public void method2() {
		System.out.println("Adapter:method2");
	}
}

//对象的适配器模式
class AdapterObj implements ISource{
	private Target target;
	AdapterObj(Target target){
		this.target = target;
	}
	@Override
	public void method1() {
		target.method1();
	}

	@Override
	public void method2() {
		System.out.println("AdapterObj:method2");
	}
}

//接口的适配器模式
abstract class Wrapper implements ISource{
	public void method1(){}
	public void method2(){}
}

class AdapterInterface1 extends Wrapper{
	public void method1(){
		System.out.println("AdapterInterface1:method1");
	}
}
class AdapterInterface2 extends Wrapper{
	public void method2(){
		System.out.println("AdapterInterface2:method2");
	}
}

