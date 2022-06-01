<template>
	<<div id="about">
		<el-table :data="tableData" style="width: 100%;">
			<el-table-column prop="date" label="日期" width="180"></el-table-column>
			<el-table-column prop="name" label="姓名" width="180"></el-table-column>
			<el-table-column prop="address" label="地址"></el-table-column>
		</el-table>
		<h3>这是一个测试页面</h3>
		
		<el-alert 
			title="成功提示的文案"
			type="success"
			effect="dark"
		></el-alert>
	</div>
</template>

<script>
</script>

<style>
</style>
