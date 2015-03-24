# tricategory

##1. 网页的表示
###1.1 网页特征
* URL深度
<p>由于主题型网页一般URL深度较大，因而URL深度可用来区分主题型网页和非主题型网页。除去URL最前面的两个‘/’，如URL最后一个字符是‘/’，则将其去除，最后剩下的‘/’的个数定义为URL的深度。如http://www.sina.com.cn/的深度为1，http://auto.sina.com.cn/news/2010-12-02/0806684129.shtml的深度为4。
* 句号的个数
<p>由于主题型网页一般包含一些完整的句子，句号较多，而非主题型网页基本不含句号。用于判断网页是否是主题型网页。
* 最大行块长度
<p>首先将网页HTML去净标签以及锚文本，只留所有正文，同时留下标签去除后的所有空白位置信息，留下的正文称为Ctext。以Ctext中的行号为轴，取其周围K行（上下文均可,K<5,这里取K=3,方向向下, K称为行块厚度），合起来称为一个行块Cblock，行块i是以Ctext中行号i为轴的行块。非主题型网页链接较多，锚文本较多，行块长度较小。用于判断网页是否是主题型网页。
* URL包含数字的个数
<p>主题型网页的URL中一般会含有网页发布的时间，从而含有较多的数字，而非主题型网页则一般不含或含较少数字。
* 链接标签所占比例
<p>非主题型网页含大量链接，而主题型网页所含链接则相对较少。

###1.2 特征向量
<p>从网页中提取上述特征，并按URL深度、句号个数、最大行块长度、URL包含数字的个数、链接标签所占比例的顺序排列，即可构成一个向量。
<p>有些特征项的取值范围可能过大，为了防止方差大的随机变量主导分类过程，需要对特征值进行缩放。这里采用的缩放方式是将特征值除以从训练集中统计得出的对应特征值的最大值，训练过程将在下面介绍。用缩放后的特征值构成的向量就是我们需要的特征向量，一个网页就被量化地表示为这样一个特征向量，如下所示：
<br>[0.14285714285714285, 0.0, 0.021011162179908074, 0.0, 0.7904191616766467]

##2. 训练
###2.1 训练数据的组织形式
<p>由于提取网页特征是需要网页的URL和源代码，所以我们将训练网页存放在一个txt文件中，其第一行存放URL，后面存放源代码，如下图所示：
<br>![train text](https://cloud.githubusercontent.com/assets/2942953/6796471/ae781e60-d22e-11e4-9199-25862a26252d.png)
<p>每个类别的训练文件放在不同的文件夹中，文件夹名为类名，如下图所示：
<br>![train dir](https://cloud.githubusercontent.com/assets/2942953/6796512/3cc396b8-d22f-11e4-874f-24094cdc9d42.png)
###2.2 训练过程
<p>依次读取不同类别文件夹下的训练文件，提取网页特征，并将其所属类别和特征值记录下来，同时在这一过程中计算训练集网页中除链接标签所占比例（链接标签所占比例的取值范围必然在0~1之间）之外的特征项的最大值并记录下来。然后对每个训练网页的特征值进行缩放，计算出其特征向量。最后将每个训练网页的类别和特征向量，以及各特征项的最大值写入到文件中，如下所示：
<br>![train dir](https://cloud.githubusercontent.com/assets/2942953/6796579/96583728-d230-11e4-8083-707bc820871b.png)
<br>![train dir](https://cloud.githubusercontent.com/assets/2942953/6796596/f8b6193a-d230-11e4-951f-600fcf534d1a.png)
##3. 分类
###3.1 kNN算法
<p>kNN分类算法是一种传统的基于统计的模式识别方法。算法思想很简单：对于一篇待分类文档，系统在训练集中找到k个最相近的邻居，使用这k个邻居的类别为该文档的候选类别。该文档与k个邻居之间的相似度按类别分别求和，减去一个预先得到的截尾阈值，就得到该文档的类别测度。可用如下公式描述：
