<%@ page contentType="text/html; charset=UTF-8" language="java" import="java.sql.*" errorPage="" %>
<%@include file="head.jsp" %>
<div class="login-top">
	<div class="container ltopCon">
    	<div class="fl">
            <div class="loginWel fl" style="background: url(../images/toplogo_small.png) no-repeat center; "></div>
            <span>欢迎使用</span></div>
        <div class="name fr"></div>
    </div>
</div>
<div class="loginBody" style="background:url(../images/1.jpg) no-repeat center center; background-size:cover;">
	<div class="container" style="position:relative; height:100%;">
        <div class="loginBox fr">
            <dl class="login_dl">
                <dt>
                    <span>好大夫网站医院信息</span>
                    <div class="fr"><a href="javascript:void(0)" onclick="PopUp1()">获取失败？联系客服申请</a></div>
                </dt>
                <dd>
                    <input class="input Text" type="text" placeholder="医院首页网址" />
                    <em>* 请输入正确网页地址</em>
                </dd>
                <dd>
                    <input class="input Password" type="password" placeholder="医院名称" />
                    <em>* 请输入正确医院名称</em>
                </dd>
                <dd>
                    <a href="javascript:void(0)" class="bt" onclick="PopUp2()">开始获取</a>
                </dd>
                <dd  class="ser">
                	<a href="javascript:void(0)" class="missPwd fl" onclick="PopUp1()">获取失败？</a>
                    <a href="javascript:void(0)" class="fr" onclick="PopUp1()"><b>联系客服</b></a>
                </dd>
            </dl>
        </div>
    </div>    
</div>
<!-----联系客服提示窗 begin----->
<div class="noticeBg PopBg1">
	<div class="xpwdBox">
        <div class="title">提示</div>
        <div class="con">申请账号或找回密码请联系0871-000000</div>
        <a href="" class="cBtn b1" >确 定</a>
        <div class="popClose" onclick="PopClose1()"></div>
    </div>
</div>
<!-----联系客服提示窗 end----->

<!-----修改初始密码提示窗 begin---程序需判断密码是不是初始密码，然后再弹出此窗-->
<div class="noticeBg PopBg2">
    <div class="xpwdBox">
        <div class="title">提示</div>
        <div class="con">初始密码比较简单，是否马上修改？</div>
        <a href="setPwd.jsp" class="cBtn b1">确 定</a>
        <div class="popClose" onclick="PopClose1()"></div>
    </div>
</div>
<!-----修改初始密码提示窗 end----->
<%@include file="footer.jsp" %>