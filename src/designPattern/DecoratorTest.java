package designPattern;

/*
 * 装饰模式就是给一个对象增加一些新的功能，而且是动态的，要求装饰对象和被装饰对象实现同一个接口，装饰对象持有被装饰对象的实例
 */
public class DecoratorTest {
	public static void main(String[] args) {
		ISourceDecorator target = new TargetDecorator();
		ISourceDecorator decorator = new Decorator(target);
		decorator.method();
	}
}

interface ISourceDecorator{ 
	public void method();
}

class TargetDecorator implements ISourceDecorator{
 
	@Override
	public void method() {
		System.out.println("Target:method");
	}
}

class Decorator implements ISourceDecorator{
	private ISourceDecorator source;
	Decorator(ISourceDecorator source){
		this.source = source;
	}
		
	@Override
	public void method() {
		System.out.println("before method");
		source.method();
		System.out.println("after method");
	}
	
}