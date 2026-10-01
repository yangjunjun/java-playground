package com.learn.oop;

/**
 * 封装：类与对象、构造器（含 this() 构造器链）、访问权限、getter/setter。
 */
public class Encapsulation {

    /**
     * 访问权限（同类 / 同包 / 子类 / 其他）：
     * private:    √   ×   ×   ×
     * 默认(default): √  √   ×   ×
     * protected:  √   √   √   ×
     * public:     √   √   √   √
     */
    static class BankAccount {
        // private 字段：外部只能通过 getter/setter 访问，便于校验与隐藏实现
        private String owner;
        private double balance;

        public BankAccount(String owner) {
            this(owner, 0); // this(...) 委托给另一个构造器，避免重复代码
        }

        public BankAccount(String owner, double balance) {
            this.owner = owner; // this 指向当前对象
            this.balance = balance;
        }

        public String getOwner() {
            return owner;
        }

        public double getBalance() {
            return balance;
        }

        /** setter 可做校验，这是封装的价值所在 */
        public void deposit(double amount) {
            if (amount <= 0) {
                System.out.println("存款金额必须为正数，已拒绝：" + amount);
                return;
            }
            balance += amount;
        }
    }

    public static void demo() {
        BankAccount acc = new BankAccount("小明");
        System.out.println("开户人 = " + acc.getOwner() + ", 余额 = " + acc.getBalance());

        acc.deposit(100);
        System.out.println("存款 100 后余额 = " + acc.getBalance());

        acc.deposit(-5);
        System.out.println("最终余额 = " + acc.getBalance());
    }
}
