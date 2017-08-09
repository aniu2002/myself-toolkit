<div class="accordion-group">
    <div class="accordion-heading">
    	<a title="" href="#collapse${counter}" data-parent="#menuDiv" data-toggle="collapse" class="accordion-toggle">
    	  <i class="icon-chevron-down"></i>&nbsp;${label?if_exists}
    	</a>
    </div>
    <div class="accordion-body collapse ${cssIn?if_exists}" id="collapse${counter}">
		<div class="accordion-inner">
			<ul class="nav nav-list">
<#list items as data>
				${data}
</#list>
			</ul>
		</div>
    </div>
</div>