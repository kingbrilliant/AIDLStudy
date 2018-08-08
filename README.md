Android跨进程通信（IPC:Inter-Process Communication）,网上总是有很多的文章来介绍，可能是我基础不够，看了不少关于Binder的源码分析，到头来也还是没有进入到Binder源码的世界，所以我还是说说自己的理解。
先看一下市面上主流APP的多进程情况，以高德地图和百度地图为例：
![image.png](https://upload-images.jianshu.io/upload_images/1766556-6f133d210ab5bde3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)
会发现这两款APP都是有多个进程的，而且IPC也属于Android中比较高阶的领域，所以多少还是了解一下。
一般来说，多进程通信的场景并不多，但是还是存在一些场景的，比如常见的推送，只要集成第三方推送服务，就会发现推送是一个单独的进程，再比如上面所说的地图APP，会存在语音播报场景，我觉得这个语音播报就可以单独放在一个进程中。业务场景多少有了一些了解，说说AIDL的原理吧。

跨进程通信，说的是进程，一个进程在操作系统上有一块地址，不同的进程是不能互相访问自己的地址的，这个地址是处在用户空间的，说起用户空间，还有一个系统空间，这里举一个不恰当的例子，想象一下，现实生活中什么场景最讲究安全，我想到的是监狱，电影里面总是有这样的场景，每间狱室里面的犯人是不可以随意出来的，外面有预警把守，想象一下，每间狱室就是一个进程，狱室和狱室之间是不可以随意通信的，要想通信，犯人先要和狱警沟通，然后狱警再来到另一间狱室，狱警再和对应的狱室进行交流，这个例子里面狱警所在的空间可以比作系统空间，整个监狱就是Android系统。监狱提供了狱警，那么Android提供了什么呢？没错，就是Binder，说了这么多就是想举个例子，加深一下理解，关于Binder，网上有各种各样的资料，其实binder翻译过来可以理解为胶水，胶水是用来粘东西的，两个东西用胶水一粘结就成为了一个，所以我们在Activity里面getSystemService就可以调用其他进程的Service了，感觉像是在当前进程一样，这就是Binder的目的，至于为什么Android选择使用Binder通信，可以去查资料。现在把上面的例子延伸一下，一个犯人想买一包烟，他会叫狱警，把钱给狱警，狱警来到监狱里面的超市，买了一包烟，然后回到狱室，把烟给犯人，这里面有三个角色，犯人、狱警和商店，犯人有买烟的需求，超市卖烟，提供这个需求，买烟需要通过狱警，CS模型出来了，犯人是Client，超市是Server，狱警是Binder。

现在开始说正题，AIDL，Android Interface Definition Language，安卓接口定义语言，这是一种编程语言，它的作用是定义接口，可以把它理解为一个翻译，翻译有输入和输出，比如中英翻译，可以把中文翻译成英文，那么AIDL的输入和输出是什么呢，输入是AIDL语音，这么说相当于是废话，AIDL是用来描述接口的，那么它把接口翻译成什么了呢，借着上面监狱的例子，商店要卖烟，所以它有卖烟的功能，而且这个烟只能通过狱警来卖，那你就得按照狱警能理解的意思来，所以要通过AIDL翻译一下：
![image.png](https://upload-images.jianshu.io/upload_images/1766556-220c5ac07cee926f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
新建一个Library，新建一个aidl文件，定义一个sellYan的方法，好了，该翻译了，build一下:
![image.png](https://upload-images.jianshu.io/upload_images/1766556-fdf520d39a588a1a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
翻译完成。下面，烟要正式上架。新建一个Service，命名SellService，代表商店，在这里实现怎么将烟交给狱警：
![image.png](https://upload-images.jianshu.io/upload_images/1766556-5583b94914f9f5e3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
是的，就这么简单，现在得把商店开业了，新建一个server的Module，这个Module依赖刚才的library：
 ![image.png](https://upload-images.jianshu.io/upload_images/1766556-af254c612375e7ee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
只是简单的声明SellService。好了商店营业了。运行这个应用就表示商店正在营业了。

下面该创建狱室了，以默认的app就可以了，我们要做的就是在这个应用内访问server里面的SellService。这里要用bindService方式了，隐式调用。再调用之前要做一件事，就是把刚才翻译后的产物原封不动的拷贝到当前工程之中，而且前提是包名必须一致：
![image.png](https://upload-images.jianshu.io/upload_images/1766556-2107c9a04f82832e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
现在开始绑定Service：
![image.png](https://upload-images.jianshu.io/upload_images/1766556-f30daef320ee2df8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
也是很简单，好，先运行server，再运行app，点击按钮看一下效果。是不是买到烟了？

下面说一下这里面的知识点吧，也就是AIDL翻译后的产物。其实说白了，跨进程通信就是需求方和服务方各自和binder打交道，怎么打交道的呢，就是AIDL翻译后的产物了，这个可以看一下这篇文章https://github.com/cundong/blog/blob/master/Android%20AIDL%20%E5%8E%9F%E7%90%86%E8%A7%A3%E6%9E%90.md
其实就是根据AIDL，结合Binder，生成一个接口，这个接口有两个实现类：Stub和Proxy，Stub在服务端，Proxy用于客户端，说白了都是代理，所以拷贝到客户端的文件必须包名一致，因为要通过binder将包名方法名传递给服务端，包名不一致是找不到对应服务的，通俗一点说，跨进程通信，就是在客户端有一套文件，在服务端有一套文件，这两套文件是一摸一样的，这样才能像在自己的应用内调用一样，而这套文件就是通过AIDL生成的。AIDL只是起到翻译的作用而已。
