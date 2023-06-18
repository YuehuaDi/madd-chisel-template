package prefetcher

import chisel3._
import chisel3.iotesters.PeekPokeTester
import chisel3.util._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class StridePrefetcherTester(c: StridePrefetcher) extends PeekPokeTester(c) {
  // 连续访问序列
  poke(c.io.pc, 0.U)
  poke(c.io.address, 0.U)
  step(1)
  expect(c.io.prefetch_address, 0.U)
  expect(c.io.prefetch_valid, false.B)

  poke(c.io.pc, 1.U)
  poke(c.io.address, 4.U)
  step(1)
  expect(c.io.prefetch_address, 12.U)
  expect(c.io.prefetch_valid, true.B)

  poke(c.io.pc, 2.U)
  poke(c.io.address, 8.U)
  step(1)
  expect(c.io.prefetch_address, 20.U)
  expect(c.io.prefetch_valid, true.B)

  // 非连续访问序列
  poke(c.io.pc, 3.U)
  poke(c.io.address, 16.U)
  step(1)
  expect(c.io.prefetch_address, 48.U)
  expect(c.io.prefetch_valid, true.B)

  poke(c.io.pc, 4.U)
  poke(c.io.address, 8.U)
  step(1)
  expect(c.io.prefetch_address, 28.U)
  expect(c.io.prefetch_valid, true.B)

  poke(c.io.pc, 5.U)
  poke(c.io.address, 24.U)
  step(1)
  expect(c.io.prefetch_address, 56.U)
  expect(c.io.prefetch_valid, true.B)

  poke(c.io.pc, 6.U)
  poke(c.io.address, 24.U)
  step(1)
  expect(c.io.prefetch_address, 24.U)
  expect(c.io.prefetch_valid, true.B)
}

class StridePrefetcherSpec extends ChiselFlatSpec {
  // 在这里指定addressWidth、pcWidth和测试的backend
  private val addressWidth = 32
  private val pcWidth = 32
  private val backendName = "firrtl"

  "StridePrefetcher" should s"work correctly with $backendName backend" in {
    Driver(() => new StridePrefetcher(addressWidth, pcWidth), backendName) { c =>
      new StridePrefetcherTester(c)
    } should be(true)
  }
}

object StridePrefetcherTester {
  def main(args: Array[String]): Unit = {
    iotesters.Driver.execute(Array(), () => new StridePrefetcher(32, 32)) {
      c => new StridePrefetcherTester(c)
    }
  }
}