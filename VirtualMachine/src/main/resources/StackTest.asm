@17
D=A
@sp
A=M
M=D
@sp
M=M+1
@17
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
D=M-D
D=M
M=0
@eq_0
D;JNE
@sp
A=M-1
M=-1
(eq_0)
@17
D=A
@sp
A=M
M=D
@sp
M=M+1
@16
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
D=M-D
D=M
M=0
@eq_1
D;JNE
@sp
A=M-1
M=-1
(eq_1)
@16
D=A
@sp
A=M
M=D
@sp
M=M+1
@17
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
D=M-D
D=M
M=0
@eq_2
D;JNE
@sp
A=M-1
M=-1
(eq_2)
@892
D=A
@sp
A=M
M=D
@sp
M=M+1
@891
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
D=M-D
D=M
M=0
@lt_0
D;JGE
@sp
A=M-1
M=-1
(lt_0)
@891
D=A
@sp
A=M
M=D
@sp
M=M+1
@892
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
D=M-D
D=M
M=0
@lt_1
D;JGE
@sp
A=M-1
M=-1
(lt_1)
@891
D=A
@sp
A=M
M=D
@sp
M=M+1
@891
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
D=M-D
D=M
M=0
@lt_2
D;JGE
@sp
A=M-1
M=-1
(lt_2)
@32767
D=A
@sp
A=M
M=D
@sp
M=M+1
@32766
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
D=M-D
D=M
M=0
@gt_0
D;JLE
@sp
A=M-1
M=-1
(gt_0)
@32766
D=A
@sp
A=M
M=D
@sp
M=M+1
@32767
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
D=M-D
D=M
M=0
@gt_1
D;JLE
@sp
A=M-1
M=-1
(gt_1)
@32766
D=A
@sp
A=M
M=D
@sp
M=M+1
@32766
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
D=M-D
D=M
M=0
@gt_2
D;JLE
@sp
A=M-1
M=-1
(gt_2)
@57
D=A
@sp
A=M
M=D
@sp
M=M+1
@31
D=A
@sp
A=M
M=D
@sp
M=M+1
@53
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
M=D+M
@112
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
@sp
A=M-1
M=-M
@sp
AM=M-1
D=M
A=A-1
M=D&M
@82
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
M=D|M
@sp
A=M-1
M=!M
