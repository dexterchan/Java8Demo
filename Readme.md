
<h1> Lock Free Linked List</h1>
<h3>Benchmark</h3>

<h5>Baseline Queues.newConcurrentLinkedQueue</h5>
Number of Thread = 10 <br>
Enqueue:
min=5.2E-5, max=0.060827, mean=0.0010290836575875487, stddev=0.0025738292154657526, median=1.19E-4, p75=0.0014775, p95=0.004883299999999999, p98=0.00637252, p99=0.006779970000000001, p999=0.059701075000000145, mean_rate=60683.69472716612, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/second, duration_unit=milliseconds
Dequeue:
min=5.2E-5, max=0.060827, mean=0.0010290787937743192, stddev=0.0025738309965213416, median=1.19E-4, p75=0.0014775, p95=0.004883299999999999, p98=0.00637252, p99=0.006779970000000001, p999=0.059701075000000145, mean_rate=60643.250658628414, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/second, duration_unit=milliseconds

<h5>LockFreeLinkedListQueue</h5>
Number of Thread = 10 <br>
min=8.6E-5, max=0.023598, mean=8.234066147859922E-4, stddev=0.0017356227496630777, median=2.42E-4, p75=5.9E-4, p95=0.0042469, p98=0.006258879999999983, p99=0.008700600000000017, p999=0.02329335500000004, mean_rate=60788.3245850754, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/second, duration_unit=milliseconds
min=8.6E-5, max=0.023598, mean=8.234066147859922E-4, stddev=0.0017356227496630777, median=2.42E-4, p75=5.9E-4, p95=0.0042469, p98=0.006258879999999983, p99=0.008700600000000017, p999=0.02329335500000004, mean_rate=60788.3245850754, m1=0.0, m5=0.0, m15=0.0, rate_unit=events/second, duration_unit=milliseconds