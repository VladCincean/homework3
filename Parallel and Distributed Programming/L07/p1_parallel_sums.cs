using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Threading;
using System.Diagnostics;

namespace ConsoleApp1
{
    class Program
    {
        private const int N = 10000;

        static void ThreadWork(int tid, int[] array, ManualResetEvent[] events)
        {
            int current = tid;
            int intervalWidth = 2;

            if (current * 2 + 1 < array.Length)
            {
                array[current * 2 + 1] += array[current * 2];
            }

            while (current % 2 == 1)
            {
                int prevTid = tid - intervalWidth / 2;
                // wait for the prev thread to finish
                events[prevTid].WaitOne();

                for (int i = 0; (i < intervalWidth) && (current * intervalWidth + i < array.Length); i++)
                {
                    array[current * intervalWidth + i] += array[prevTid * 2 + 1];
                }

                current /= 2;
                intervalWidth *= 2;
            }

            events[tid].Set();
        }
        static int[] PartialSums(int[] array)
        {
            int[] result = new int[array.Length];
            List<Thread> threads = new List<Thread>();
            int nrThreads;
            ManualResetEvent[] events;

            // nrThreads := 2^k, where 2^k <= len(array) < 2^(k+1)
            nrThreads = 1;
            while(nrThreads < array.Length)
            {
                nrThreads *= 2;
            }
            nrThreads /= 2;

            events = new ManualResetEvent[nrThreads];

            for (int i = 0; i < nrThreads; i++)
            {
                events[i] = new ManualResetEvent(false);
            }

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Start();

            for (int i = 0; i < nrThreads; i++)
            {
                int tid = i;
                Thread thread = new Thread(
                    () => ThreadWork(tid, array, events)
                );
                threads.Add(thread);
                thread.Start();
            }

            foreach(Thread t in threads)
            {
                //Console.WriteLine("aaa");
                t.Join();
            }

            stopwatch.Stop();

            Console.WriteLine("time: {0} ms", stopwatch.ElapsedMilliseconds);

            return result;
        }

        static void Main(string[] args)
        {
            int[] a = new int[N];

            for (int i = 0; i < N; i++)
            {
                a[i] = i;
            }

            PartialSums(a);

            for (int i = 0; i < N; i++)
            {
                Console.Write(a[i].ToString());
                Console.Write(" ");
            }
            Console.Write("\n");
        }
    }
}
