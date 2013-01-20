<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://jchome.jsprun.com/jch" prefix="jch"%>
<jsp:include page="${jch:template(sConfig, sGlobal, 'header.jsp')}"/>
<c:if test="${op == 'use'}">
	<c:if test="${!frombuy}" >
	<h1>使用道具</h1>
	<a class="float_del" title="关闭" href="javascript:hideMenu();">关闭</a>
	</c:if>
	<div class="toolly" id="__magicuse_form_${mid }">
		<form method="post" id="magicuse_form_${mid }" action="magic.jsp?mid=${mid }&idtype=${idtype }&id=${id }">
			<div class="magic_img">
				<img src="image/magic/${mid }.gif" alt="${magic.name }" />
			</div>
			<div class="magic_info">
				<h3>${magic.name }</h3>
				<p class="gray">${magic.description }</p>
				<p>拥有个数: ${usermagic.count }</p>
				<div>
					<h4>选择样式</h4>
					<ul>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper1';$('paper1').click();">
							<div for="paper1"><img src="image/magic/paper/1.preview.gif" alt="" /></div>
							
							<input id="paper1" type="radio" name="paper" value="1" checked />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper2';$('paper2').click();">
							<div for="paper2"><img src="image/magic/paper/2.preview.gif" alt="" /></div>
							
							<input id="paper2" type="radio" name="paper" value="2" />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper3';$('paper3').click();">
							<div for="paper3"><img src="image/magic/paper/3.preview.gif" alt="" /></div>
							
							<input id="paper3" type="radio" name="paper" value="3" />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper4';$('paper4').click();">
							<div for="paper4"><img src="image/magic/paper/4.preview.gif" alt="" /></div>
							
							<input id="paper4" type="radio" name="paper" value="4" />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper5';$('paper5').click();">
							<div for="paper5"><img src="image/magic/paper/5.preview.gif" alt="" /></div>
							
							<input id="paper5" type="radio" name="paper" value="5" />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper6';$('paper6').click();">
							<div for="paper6"><img src="image/magic/paper/6.preview.gif" alt="" /></div>
							
							<input id="paper6" type="radio" name="paper" value="6" />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper7';$('paper7').click();">
							<div for="paper7"><img src="image/magic/paper/7.preview.gif" alt="" /></div>
							
							<input id="paper7" type="radio" name="paper" value="7" />
							选择
						</li>
						<li style="float:left; width:60px; text-align:center; margin:4px; cursor:pointer;" onclick="$('blog_article').className = 'article magicpaper8';$('paper8').click();" >
							<div for="paper8"><img src="image/magic/paper/8.preview.gif" alt=""/></div>
							<input id="paper8" type="radio" name="paper" value="8" />
							选择
						</li>
					</ul>
					<br style="clear:both;" />
				</div>
				<p class="btn_line">
					<input type="hidden" name="formhash" value="${jch:formHash(sGlobal,sConfig,false)}" />
					<input type="hidden" name="refer" value="${sGlobal.refer }"/>
					<input type="hidden" name="usesubmit" value="1" />
					<input type="submit" name="usesubmit_btn" value="使用" class="submit" />
				</p>
			</div>
		</form>
	</div>
</c:if>
<jsp:include page="${jch:template(sConfig, sGlobal, 'footer.jsp')}"/>