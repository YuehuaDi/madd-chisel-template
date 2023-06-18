package prefetcher

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class StridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  val io = IO(new Bundle {
    val pc = Input(UInt(pcWidth.W))
    val address = Input(UInt(addressWidth.W))
    val prefetch_address = Output(UInt(addressWidth.W))
    val prefetch_valid = Output(Bool())
  })

  // 查找表格
  val tableSize = 1024 // 表格大小
  val strideTable = SyncReadMem(tableSize, SInt((addressWidth - 2).W)) // 使用有符号整数存储步幅信息
  val index = io.pc(addressWidth - 1, 2) // 使用 PC 的高位作为索引
  val stride = strideTable.read(index) // 从表格中读取当前步幅值

  // 步幅计算
  val lastAddress = RegNext(io.address) // 存储上一个访问地址
  //val strideCalc = io.address - lastAddress // 当前地址与上一个地址之差，即步幅
  val strideCalc = io.address.asSInt - lastAddress.asSInt
  strideTable.write(index, strideCalc) // 更新步幅表格

  // 预取地址生成
  val prefetchOffset = RegInit(0.U(addressWidth.W)) // 预取地址的偏移量
  val prefetchAddress = io.address + (stride << 2).asUInt() + prefetchOffset // 使用步幅计算预取地址
  io.prefetch_address := prefetchAddress
  io.prefetch_valid := true.B

  // ...
}

object StridePrefetcherMain extends App {
  val addressWidth = 32
  val pcWidth = 32

}
