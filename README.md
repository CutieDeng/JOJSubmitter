# 读我读我

<div align="right">
Thu Jan 27 10:03:20 CST 2022
</div>


Version/Tag: v1.1

---

## 目录

[toc]



## 前言

在一切开始之前，让我们先讲个故事吧。

故事发生在盛夏的一个平凡的日子，在 SUSTech 教学楼的某间教室里，孩子们在教室里做着 DSAA 的 lab 作业。一切安详又美好。

如果故事就这样平凡地进行下去，大家都规规矩矩地做完了作业，那自然是没有什么看头的。这篇引言也大致可以说就结束了。

但事实不然，在 SUSTech Online Judge 网站上，有着许许多多的练习题要我们去完成。而除了阅读题目，做适当的翻译，转换成形式化的描述，推导证明易于导出答案的有关性质，写出能够解决问题的程序代码以外，我们还有不可或缺的一步：将写出的程序代码上交到 OJ 上。

而这个 OJ 呀，有点小小的乖张和高傲——它要求你必须只上传一份代码。

考虑到 Java 是一门面向对象的程序设计语言，以类作为解决问题的基本单位，因此你需要将解决方案的类名修改成 Main, 并且如果你写了多个类——你需要将它们全部一一拷贝到同一个文件下，并相应地检查运行结果，以便于你确认「聚拢」后的代码没有权限、语法等方面的错误。

众所周知，这个过程枯燥无味，作为一名 CS 学生，有什么理由忍受这种愚蠢的时间浪费呢？

因此，某名同学突然突发奇想，实验性地制造了一个通过类似宏命令的特殊描述，指示性地聚合了多个类文件！

很难得因着一点阴阳差错，一个些许带着取乐、玩票性质的项目，渐渐成型并开始成长和发展。

正所谓：己欲立而立人，己欲达而达人。兴许是带着些为未来同学谋福利的想法吧，我们决定实现这样一个简单的 Java 插件。

那么，开始吧。



## 项目简介





程序设计语言代码重整器，根据指定的文件目标 (File Target) 提取相应的代码，去除 package 描述并重整、封装类名描述。
同时，import 的相关描述会被处理，并智能分析其 import 的包名描述——当且仅当该包名、引用类是自行实现时，才对其进行处理，否则不处理！

> Java 标准中，包名和类名不允许相同。

**封装设计描述**

这为我们通过虚拟 `枚举类` 来等价地表示具体类提供了方便。
之所以选择枚举类，而不选择使用 interface, 则是因为尽管他们都不能被直接实例化，但枚举类由于不可被继承，具有更好地封装性——接口依旧可以被用来做实现，尽管这种可能性很低。
但尽可能遏制充满好奇心的孩子去发掘漏洞不失是一种很重要的软件设计、开发原则。

例如，考虑解决 *a + b* 问题的代码

```java
package problem.lab01; 

public class Solution4APlusB {
  public static void main(String[] args) {
    int a, b; 
    final Scanner input = new Scanner(System.in); 
    try { 
      a = input.nextInt(); 
      b = input.nextInt(); 
    } catch (RuntimeException r) {
      System.err.println("Expect integers but failed. "); 
      return ; 
    } 
    System.out.println(a + b); 
  }
}
```

我们将自动重整这段代码，结果如下：

```java
enum problem {
  enum lab01 {
    public class Solution4APlusB {
      public static void main(String[] args) {
        // The content is the same as above. 
      } 
    }
  }
}

public class Main {
  public static void main(String[] args) {
    problem.lab01.Solution4APlusB.main(args); 
  }
} 
```

这个设计来自于之前所阐述的一个特殊的规则，Java 编译器要求类全名与包全名不能完全一致。
这意味着我们可以通过虚拟类的方式，将包跟一个高度抽象的类绑定在一起！
尽管我们有很多种选择——设计普通的类、抽象类、不可继承类、枚举类、接口、记录……但最终我们选择了既无法直接实例化，也无法间接继承的枚举类型。
这是我们对类重整的描述规范。

特别地，当程序运行过程不涉及到同一个包下多个类的使用情形时，我们可以简单地省略掉做该修饰的操作。
同理，但我们涉及的源代码都在同一个包下，我们可以考虑省略该包的虚拟化。



## 项目目标

本项目的目标是实现一个 Java 源代码聚合器，能够智能地将 Java 程序代码进行聚拢，以便于形成一份简单的 Java 代码文件以便于提交到 OJ 上。

其实，也许除了 SUSTech OJ 会反人性地设计这样一个作业提交系统以外，可能任何其他地方都不会考虑使用这种方式作为成果检验的标杆吧。

当然，在工程领域，本身书写 Java 代码的时候，包管理系统就是对项目代码的一种较好的管理系统，能够通过类似树型的层次划分来将不同类型、不同功能的代码划分在不同区块。

所以可想而知，将代码聚拢的功能实现意义寥寥——然而，我们的 OJ 目前为止，确实是这样平庸，在这种情况下，我们计划设计的插件，正是听从了这种不便的召唤，提供了一个小小的便利。（也许是更大的不便。

在设计上，我们的程序目前大致划分为如下模块：

1. 注释过滤器：过滤掉原有代码中的注释，并用空白符加以填充。
2. 代码切割器：正确地切割目标文件的代码块，以便划分相关的代码区段，大致划分为空白代码段、关键字段、自定义字符段、运算符段和作用域约束段！
   与此同时，代码切割的过程会保护行相关的信息描述！每行的字符串信息都会维护一个前缀空白字符长度，并与其他行做差分以便插入代码时自动调整行间距！
3. 代码文件描述器：负责扫描代码切割器切割出的有效代码内容，并针对单个文件记录其相关信息，包括 `package` 内容，可以识别的 `import` 信息，需要推断、决议的 *import compack.classpack.\** 情形。
4. 方法与类标记器：根据代码文件描述信息，获取代码切割器的内容，并重新标记与包、类相关的信息，记录所有类名出现的情形，将其与 *import clause* 进行对应，并将其「全名化」。
5. 文件内容重整修饰器：删除 package 信息和可被处理的 import 语句，保留其余 import 子句，并划分该文件的 public class 的起始和结束范围！
6. 聚合器：将所有的文件修饰内容根据其记录在修饰器中的 package 信息进行逐个聚合，并生成对应的 `enum` 包装将其合拢，最后在头部生成一个普通的 `Main` 主类，并指示主方法直接调用目标文件的主方法！

下面我们逐个模块进行其相应的决定。



## 注释过滤器

现先研究注释过滤器的注释规则！

考虑到一般字符串内注释的指令将会失效，我们有必要对字符串做特殊的判定，因此，注释过滤器将会是三个简单有限状态自动机的聚合！

字符串标识有限状态自动机，行注释标识有限状态自动机和块注释标识有限状态自动机。

显而易见，对于字符串标识自动机来说，其有这样三种状态：非字符串标识（inactive），字符串标识（active）和转义标识（active）。

行注释、块注释的状态分析同理，在此不多赘述。

所有的过滤器我们将统一用 `AbstractFilterInputStream` 来描述，其将会直接继承自 `InputStream`. 你可以尝试将多个过滤器拼接使用，形成一个过滤管道，以便于整理出更妥当的文件形式！

> 该规则已经被弃置，现 `AbstractFilterInputStream` 将会直接继承自 `Reader`, 由于其依旧是在对 *Stream* 进行内容修饰，所以依旧建议重写 `read` 方法。
>
> 尽管我对 `Reader` 的功能依旧有一定的担忧和茫然。
>
>
> <div align="right">
> 	Thu Jan 27 14:38:57 CST 2022<br>
>   Cutie Deng
> </div>

例如，考虑制表符的格式不便处理，可以通过简单的 Filter 将一个制表符等价地转化为 2/4/6 个空白符，以便于更易调整代码的格式和规范。

---

特别地，`JNotesEraserP` 是一个强化的注释过滤器实现，在处理注释以外，它顺带设置了一个标记值 0 以便于描述换行符的位置，以便于让行缩进控制器能很快获得其位置，而不需要自行判断换行符的位置。
这是一个不在标准叙述中的实现方式，不过出于高效期见，建议开发者能够在此给出对快速换行的高效处理！

## 行缩进控制器

这其实是突然一拍脑门想到的特殊设计，所以在上文中并没有出现。（我以后会补充上去的。

考虑到出于最后管理、整合和拼接代码的便利性，我们需要一个专门用于控制代码缩进情形的描述器！

例如，对于如下代码：

```java
public class Special {
  public static void main(String[] args) {
    int x = 21; 
    if (x % 2 == 0) {
      System.out.println("x is even. "); 
    } else {
      System.out.println("x is odd. "); 
    } 
  } 
}
```

当我们考虑将其置于一个枚举类下时——我们期望有这样的变化：

```java
enum Base {
  public static class Special {
    public static void main(String[] args) {
      int x = 21; 
      if (x % 2 == 0) {
        System.out.println("x is even. "); 
      } else {
        System.out.println("x is odd. "); 
      } 
    }
  }
}
```

使其能够智能地进行缩进以便于其符合人类的代码阅读习惯。

行缩进控制器的约定如下：

- 行缩进控制器不会处理制表符，在将字符流交付给行缩进控制器之前，请自行将制表符全部过滤！对制表符的处理取决于具体实现而不取决于约定。
- 行缩进控制器在处理字符时，如果有异常发生，控制器应当尽量先关闭字符流再抛出其对应的异常。如果同时在关闭字符流时有异常发生，优先汇报先前发生的异常，因为往往早期的异常更具参考价值。
  处理字符的贪婪性质不做要求，这意味着你不一定要做预处理，你同样可以惰性地解决该问题。尽管笔者之前的约定是预处理。
- 行缩进控制器处理结束后，将会提供一个 `nextLine` 方法描述该行信息。该行的信息通过一个 `Record` 记录，描述的信息包括该行缩进的空格数目（以 `int` 来记录）和该行实质的内容信息（以 `String` 来记录）。
- 对于空行，`nextLine` 会返回一个内容为空字符串的 `Record` 来描述它，缩进空格数目应当被设为 $0$。
  值得注意，返回的是空字符串，而不是一个 `null pointer`. 
- 当控制器读取文件尾部的时候，将返回一个 `null` 表示其后再无内容读入！
- 行缩进控制器的实现应当继承自 `AbstractIndentationController`, 其应当给出 `nextLine` 和 `close` 的实现，以便于更好地进行资源管理。



## 代码切割器

代码切割器是从语义上对代码进行划分的重要工具，也是我们需要重整代码过程中的重要手段。

代码切割器将会定义一个特殊的链表结构：其将一行代码组织成两个链表，一个 *token* 链表，一个空白符链表，并根据其内容顺序依次交织在一起。

这样该切割器便能迅速还原原先的代码设计情形，又能够快速地将有意义、可识别的代码组织起来！

由于我们先前便将代码按行划分开，接下来只需要简单地通过贪婪匹配特殊关键字，便能够很高效地划分它们。

但在此之前，我们不得不先约定一个统一的特殊结构，以便方便地描述所有 *token*. 

该结构为特殊的双端队列，分为两种元素：空白元素和关键字元素！

由于在此前已经约定不允许有制表符的出现，同时我们已经对代码进行了行划分，所以代码中的**空白元素**只会包含空白符！

因此，只需要一个简单的 `int` 类型，便能够描述一个空白元素的具体内容，即有多少个空白元素在其中。

而关键字元素则通过一个 `String` 类型描述其内容，特别地，一个关键字会简短的提供微量的信息，描述一个「主语」、「定语」、「谓语」等等，但绝不会描述超过两个以上的语素！

而值得注意的是，无论是何种元素，都会持有这四个指针描述，以便于其在接下来的使用中快速跳转！

特别规定，由于在此之前已经进行了行缩进控制，因此无论是获取哪一行的字符串，其「队列」的第一个元素都应当是一个关键字元素，以便于接下来的快速访问！



