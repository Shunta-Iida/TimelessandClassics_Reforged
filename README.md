# Timeless and Classics Reforged

This repository is made from

- [Timeless and Classics Reforged](https://github.com/286799714/TimelessandClassics_Reforged)

Realistic and many features including guns, attachments, grenades, armor rigs.

## Features
- 日本語化
- 武器の発する音をすべてモノラルに→Sound Physics Remastaredとの連携強化
- 個人的な用途で Escape from Tarkov ライクな要素を追加しています
  - 弾数、射撃モード確認のUI

## TODO

- [ ] TaC定義イベントとイベントリスナーの整理

  いまのところ必要そうなイベント

  - [ ] GunShotEvent
  - [ ] GunReloadEvent
- [ ] ゲストプレイヤー側ログイン時のRig同期
- [ ] GunModifierHelper, GunのメソッドをGunItemHelper, GunItemに移行するなどリファクタ
- [ ] エンチャントテーブルでのエンチャント実装
- [ ] サバイバルインベントリにリグのタブ追加
- [ ] フラッシュライトの実装
  - [ ] シェーダー用のカスタムuniform実装（oculusを改造する必要あり？）
- [ ] マガジンの追加
  - [ ] マガジンへの弾込め
  - [ ] 武器の耐久力
