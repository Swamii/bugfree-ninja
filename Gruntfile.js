module.exports = function(grunt) {

    require('load-grunt-tasks')(grunt);

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        devPath: 'app/assets/',
        servePath: 'public/dist/',

        sass: {
            dist: {
                files: {
                    '<%= servePath %>main.css': '<%= devPath %>stylesheets/main.sass'
                }
            }
        },

        jshint: {
            all: [
                'Gruntfile.js',
                '<%= devPath %>javascripts/**/*.js'
            ]
        },

        concat: {
            dev: {
                src: ['<%= devPath %>javascripts/app.js', '<%= devPath %>javascripts/**/*.js'],
                dest: '<%= servePath %>app.js'
            }
        },

        watch: {
            sass: {
                files: 'app/assets/stylesheets/**/*.sass',
                tasks: ['sass']
            },
            scripts: {
                files: ['Gruntfile.js', '<%= devPath %>javascripts/**/*.js'],
                tasks: ['jshint', 'concat:dev'],
                options: {
                    interrupt: true
                }
            }
        }

    });

    grunt.registerTask('default', ['jshint', 'concat:dev', 'sass', 'watch']);
};