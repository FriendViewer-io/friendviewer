for  %%i in (*.proto) do protoc.exe -I=%CD% --java_out=%CD% "%%i"