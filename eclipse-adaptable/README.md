Eclipse采用“平台+插件”的体系结构，平台仅仅作为一个容器，所有的业务功能都封装在插件中，通过插件组件构建开发环境。

![img](http://www.blogjava.net/images/blogjava_net/xujun7/eclipse.jpg)

Platform Runtime平台运行库是内核，Eclipse所有的功能就是通过这个runtime和插件一起完成。 在这个runtime里，定义Eclipse的核心功能。`IAdaptable`是Eclipse的核心模式，是Eclipse扩展机制的核心，就是这个简单的IAdaptable接口奠定了整个Eclipse扩展平台的基础，对Eclipse开发者来说，这个接口就像Java的Exception，Object一样，无处不在。

看开发示例前，确保已理解[从Eclipse平台看交易平台化](https://developer.aliyun.com/article/38)里讲的思想，否则食之无味。

## 开发示例

1. 首先开发业务功能，需要定义业务领域对象， 这里用IOrder接口来实现：

   ```java
   package com.gtw.adaptable.domain;
   
   import java.util.List;
   
   import com.gtw.adaptable.common.IAdaptable;
   
   public interface IOrder extends IAdaptable {
       public void addItem(DrinkType drinkType, int shots, boolean iced);
       public List<OrderItem> getItems();
       public void putAttribute(String key, Object value);
   		public String getType();
       public Object getAttribute(String key);
   }
   
   ```

   可以看到`IOrder`接口扩展了一个`IAdaptable`接口，后续可以看到这个接口的功能作用。 

2. 如何扩展开发领域功能

   假设下单系统需要增加公告通知功能。 按照以前的做法，我们会去IOrder接口里增加和超时或者优惠相关的接口方法。

   这样带来的问题是`IOrder`接口会随着业务功能的增加而膨胀，而且接口作为协议，已经对外暴露了，对基础接口的修改会导致依赖这个接口的上层代码的修改。 

   作为核心类，应该尽可能保持它的稳定，这对业务系统的稳定和健壮也有很大的好处。

   `IAdaptable`提供了一个优雅的实现让我们可以不改变`IOrder`接口，却可以给`IOrder`接口增加扩展的功能。

3. 需要定义一个`Ump`接口，定义公告相关的功能：

   ```java
   public interface Ump extends UmpComponent {
   
       void start();
   
       void close();
       
       void setOrderPromation(IOrder order);
   
       float getOrderPromation(IOrder order);    
   
   }
   ```

4. 实现一个`UmpAdapterFactory`工厂类，用来向平台注册`IOrder`的扩展功能

   ```java
   public class UmpAdapterFactory implements IAdapterFactory {
   
   	private final UmpsService umpsService;
   
   	@Autowired
       public UmpAdapterFactory(UmpsService umpsService) {
   		this.umpsService = umpsService;
   
   	}
   
   	@Override
   	public Object getAdapter(Object adaptableObject, Class adapterType) {
   		if (adapterType == Ump.class) {
   			IOrder order = (IOrder) adaptableObject;
   			Ump ump = umpsService.ump(order.getType());
   			if (ump== null) {
   				ump = umpsService.ump("general");
   			}
   			return ump;
    		}
   		return null;
   	}
   
   	@Override
   	public Class[] getAdapterList() {
   		return new Class[] { Ump.class };
   	}
   
   }
   ```

5. 业务上层通过`Ump ump = (Ump)order.getAdapter(Ump.class);` 

   这样`ump`接口通过过`order`的`getAdapter`的方式返回，在上面`ump`模快里`UmpAdapterFactory`里， 解释了会根据order的type类型，返回对应的实现，这里会返回不同`ump`的实现。



