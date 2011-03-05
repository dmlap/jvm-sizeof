define 'jvm-sizeof', :version=>'0.1' do
  repositories.remote << 'http://www.ibiblio.org/maven2/'
  
  package(:jar).with :manifest=>{ 'Premain-Class'=>'com.github.dmlap.sizeof.SizeOf' }
end