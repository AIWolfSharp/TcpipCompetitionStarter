# TcpipCompetitionStarter
TCP/IP接続版(0.3.x対応) CompetitionStarter

[AIWolfCompetitionStarter](https://github.com/carl0967/AIWolfCompetitionStarter)
をTCP/IP接続版に改造したものです．
従来通りのハードコーディングしたJavaのプレイヤークラスはもちろんのこと，
他所からTCP/IPで接続するプレイヤー（言語を問わず）の参加が可能です．
もちろんずべて外部からの接続もOKです．

### 使用法

  ```
  [-e] [-n playerNum] [-g gameNum] [-p port]
  ```
    
  　　playerNum : プレイヤー数（デフォルトは15）．ハードコーディングプレイヤーで足りない場合には外部からの接続を待ちます．
    
 　　gameNum : ゲーム回数（デフォルトは1000）

 　　port : ポート番号（デフォルトは10000）
   
　　-e オプションで，ハードコーディングプレイヤーを使わず，すべて外部からの接続となります