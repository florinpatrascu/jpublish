# testing HAML with JRuby [florin 2010.09.23]
require 'java'

require 'rubygems'
require 'date'
require 'haml'

Haml::Engine.new( haml_view).render.to_s
