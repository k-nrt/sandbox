		ORG     0100H        ; プログラムを置くアドレスの指定
;        FRE     $1000,$1FFF     ; 指定した範囲のメモリへの書き込み許可

AHO		EQU     1000H

        CALL    TEST            ; TESTをサブルーチンコール
        RET                     ; プログラムの終了を意味する

		DB     0,1 << 2,2<<4,4
		DS     "helo wold"

;       ASM

TEST:                           ; ラベル
        LD      HL,$1000        ; HLに1000Hを代入
        LD      A,0xFF           ; AにFFHを代入（$FF以外に変えてみたりしてください）
        LD      B,32            ; Bに32を代入（32以外に変えてみたりしてください）
L_TEST:                         ; ラベル
        LD      (HL),A          ; HLが指すアドレスにAの内容を書き込む
        INC     HL              ; HLに1を足す
        DEC     B               ; Bから1を引く
        JP      NZ,L_TEST       ; 上の"DEC B"でBが0にならないならL_TESTへジャンプ
        RET                     ; サブルーチンからリターン
 
