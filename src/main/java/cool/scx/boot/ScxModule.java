package cool.scx.boot;

/**
 * 模块
 */
public interface ScxModule {
    default void onStart(){
        var aClass = this.getClass();
        System.out.println(aClass.getName()+" onStart !!!");
    }

    default void onEnd(){
        var aClass = this.getClass();
        System.out.println(aClass.getName()+" onEnd !!!");
    }
}
