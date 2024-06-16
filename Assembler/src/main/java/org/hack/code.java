package org.hack;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-13 16:33
 * @version: 1.0
 */
public class code {
    private String dest;
    private String comp;
    private String jump;

    private Map<String, Byte[]> destMap = new HashMap<>();
    private Map<String, Byte[]> compMap = new HashMap<>();
    private Map<String, Byte[]> jumpMap = new HashMap<>();
    code(){
        init();
    }
    code(String dest, String comp, String jump){
        this.dest=dest;
        this.comp=comp;
        this.jump=jump;
        init();
    }

    private void init(){
        //初始化助记符
        //dest
        destMap.put("null",new Byte[]{0,0,0});
        destMap.put("M",new Byte[]{0,0,1});
        destMap.put("D",new Byte[]{0,1,0});
        destMap.put("MD",new Byte[]{0,1,1});
        destMap.put("A",new Byte[]{1,0,0});
        destMap.put("AM",new Byte[]{1,0,1});
        destMap.put("AD",new Byte[]{1,1,0});
        destMap.put("AMD",new Byte[]{1,1,1});

        //comp

        compMap.put("0",new Byte[]{0,1,0,1,0,1,0});
        compMap.put("1",new Byte[]{0,1,1,1,1,1,1});
        compMap.put("-1",new Byte[]{0,1,1,1,0,1,0});
        compMap.put("D",new Byte[]{0,0,0,1,1,0,0});
        compMap.put("!D",new Byte[]{0,0,0,1,1,0,1});
        compMap.put("-D",new Byte[]{0,0,0,1,1,1,1});
        compMap.put("D+1",new Byte[]{0,0,1,1,1,1,1});
        compMap.put("D-1",new Byte[]{0,0,0,1,1,1,0});

        //a=0
        compMap.put("A",new Byte[]{0,1,1,0,0,0,0});
        compMap.put("!A",new Byte[]{0,1,1,0,0,0,1});
        compMap.put("-A",new Byte[]{0,1,1,0,0,1,1});
        compMap.put("A+1",new Byte[]{0,1,1,0,1,1,1});
        compMap.put("A-1",new Byte[]{0,1,1,0,0,1,0});
        compMap.put("D+A",new Byte[]{0,0,0,0,0,1,0});
        compMap.put("D-A",new Byte[]{0,0,1,0,0,1,1});
        compMap.put("A-D",new Byte[]{0,0,0,0,1,1,1});
        compMap.put("D&A",new Byte[]{0,0,0,0,0,0,0});
        compMap.put("D|A",new Byte[]{0,0,1,0,1,0,1});
        //a=1
        compMap.put("M",new Byte[]{1,1,1,0,0,0,0});
        compMap.put("!M",new Byte[]{1,1,1,0,0,0,1});
        compMap.put("-M",new Byte[]{1,1,1,0,0,1,1});
        compMap.put("M+1",new Byte[]{1,1,1,0,1,1,1});
        compMap.put("M-1",new Byte[]{1,1,1,0,0,1,0});
        compMap.put("D+M",new Byte[]{1,0,0,0,0,1,0});
        compMap.put("D-M",new Byte[]{1,0,1,0,0,1,1});
        compMap.put("M-D",new Byte[]{1,0,0,0,1,1,1});
        compMap.put("D&M",new Byte[]{1,0,0,0,0,0,0});
        compMap.put("D|M",new Byte[]{1,0,1,0,1,0,1});


        //jump
        jumpMap.put("null",new Byte[]{0,0,0});
        jumpMap.put("JGT",new Byte[]{0,0,1});
        jumpMap.put("JEQ",new Byte[]{0,1,0});
        jumpMap.put("JGE",new Byte[]{0,1,1});
        jumpMap.put("JLT",new Byte[]{1,0,0});
        jumpMap.put("JNE",new Byte[]{1,0,1});
        jumpMap.put("JLE",new Byte[]{1,1,0});
        jumpMap.put("JMP",new Byte[]{1,1,1});
    }
    public Byte[] transformDest(){
        if (dest==null){
            System.out.println("dest is null");
            return null;
        }
        if (!destMap.containsKey(dest)){
            System.out.println("dest is not contain");
            return null;
        }
        return destMap.get(dest);
    }
    public Byte[] transformComp(){
        if (comp==null){
            System.out.println("comp is null");
            return null;
        }
        if (!compMap.containsKey(comp)){
            System.out.println("comp is not contain");
            return null;
        }
        return compMap.get(comp);
    }
    public Byte[] transformJump(){
        if (jump==null){
            System.out.println("jump is null");
            return null;
        }
        if (!jumpMap.containsKey(jump)){
            System.out.println("jump is not contain");
            return null;
        }
        return jumpMap.get(jump);
    }
}
