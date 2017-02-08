module.exports = function(grunt) {
	// Project configuration.
	require('load-grunt-tasks')(grunt);

	grunt
			.initConfig({
				compress : {
					main : {
						options: {
							archive: 'MoviesService.zip',
							pretty: true
						},
						createDir : true,
						exclusions : [ '.gitignore' ],
						expand : true,
						cwd : './',
						src : [ 'index.js', 'Gruntfile.js', '*.json', 'node_modules/**/*.*' ],
						dest : './'
					}
				}
			});

	grunt.registerTask('default', [ 'compress' ]);
};
