@256
D=A
@SP
M=D
@1
D=A
@LCL
M=D
@2
D=A
@ARG
M=D
@3
D=A
@THIS
M=D
@4
D=A
@THAT
M=D
@bootstrap
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Sys.init
0;JMP
(bootstrap)
@0
D=A
@sp
A=M
M=D
@sp
M=M+1
@0
D=A
@LCL
D=D+M
@R15
M=D
@sp
AM=M-1
D=M
@R15
A=M
M=D
($LOOP_START)
@0
D=A
@ARG
A=D+M
D=M
@sp
A=M
M=D
@sp
M=M+1
@0
D=A
@LCL
A=D+M
D=M
@sp
A=M
M=D
@sp
M=M+1
@sp
AM=M-1
D=M
A=A-1
M=D+M
@0
D=A
@LCL
D=D+M
@R15
M=D
@sp
AM=M-1
D=M
@R15
A=M
M=D
@0
D=A
@ARG
A=D+M
D=M
@sp
A=M
M=D
@sp
M=M+1
@1
D=A
@sp
A=M
M=D
@sp
M=M+1
@sp
AM=M-1
D=M
A=A-1
M=M-D
@0
D=A
@ARG
D=D+M
@R15
M=D
@sp
AM=M-1
D=M
@R15
A=M
M=D
@0
D=A
@ARG
A=D+M
D=M
@sp
A=M
M=D
@sp
M=M+1
@SP
AM=M-1
D=M
@$LOOP_START
D;JNE
@0
D=A
@LCL
A=D+M
D=M
@sp
A=M
M=D
@sp
M=M+1
