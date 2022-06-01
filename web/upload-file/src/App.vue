<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <HelloWorld msg="Welcome to Your Vue.js App"/>
  <!-- <UploadComponent msg="上传文件组件"/> -->
</template>

<script>
	import HelloWorld from './components/HelloWorld.vue';
	// import UploadComponent from './view/upload.vue';

	export default {
		name: 'App',
		components: {
			HelloWorld,
			// UploadComponent
		}
	}
</script>

<style>
	#app {
		font-family: Avenir, Helvetica, Arial, sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		text-align: center;
		color: #500a4f;
		margin-top: 60px;
	}
</style>
