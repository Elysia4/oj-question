-- 使用elyoj数据库
USE elyoj;

-- 插入测试用户数据
INSERT INTO user (username, password, email, role) VALUES 
('user1', 'e10adc3949ba59abbe56e057f20f883e', 'user1@example.com', 'user'),  -- 密码：123456
('user2', 'e10adc3949ba59abbe56e057f20f883e', 'user2@example.com', 'user'),  -- 密码：123456
('teacher1', 'e10adc3949ba59abbe56e057f20f883e', 'teacher1@example.com', 'user'),  -- 密码：123456
('student1', 'e10adc3949ba59abbe56e057f20f883e', 'student1@example.com', 'user');  -- 密码：123456

-- 插入测试题目数据
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, difficulty, userId) VALUES
('两数之和', '给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。', 
'["数组", "哈希表"]', 
'使用哈希表存储每个元素的值和索引，遍历数组，对于每个元素，检查target-nums[i]是否存在于哈希表中。',
'[{"input": "[2,7,11,15]\\n9", "output": "[0,1]"}, {"input": "[3,2,4]\\n6", "output": "[1,2]"}]',
'{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 128}',
0, 1),

('反转链表', '反转一个单链表。', 
'["链表", "递归"]', 
'可以使用迭代或递归方式实现链表反转。迭代方式需要使用三个指针记录前一个节点、当前节点和下一个节点。',
'[{"input": "[1,2,3,4,5]", "output": "[5,4,3,2,1]"}, {"input": "[1,2]", "output": "[2,1]"}]',
'{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 128}',
1, 1),

('二叉树的最大深度', '给定一个二叉树，找出其最大深度。二叉树的深度为根节点到最远叶子节点的最长路径上的节点数。', 
'["树", "深度优先搜索", "广度优先搜索"]', 
'可以使用递归或迭代的方式求解。递归方式：如果树为空，返回0；否则返回max(左子树深度, 右子树深度) + 1。',
'[{"input": "[3,9,20,null,null,15,7]", "output": "3"}, {"input": "[1,null,2]", "output": "2"}]',
'{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 128}',
1, 1),

('合并两个有序数组', '给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。', 
'["数组", "双指针"]', 
'从后向前比较并填充数组。设置两个指针分别指向两个数组的末尾，比较两个指针的值，将较大的值放入nums1的末尾。',
'[{"input": "[1,2,3,0,0,0]\\n3\\n[2,5,6]\\n3", "output": "[1,2,2,3,5,6]"}, {"input": "[1]\\n1\\n[]\\n0", "output": "[1]"}]',
'{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 128}',
0, 1);

-- 插入用户提交数据
INSERT INTO question_submit (language, userId, questionId, code, judgeInfo, status) VALUES
('java', 2, 1, 'public class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> map = new HashMap<>();\n        for (int i = 0; i < nums.length; i++) {\n            int complement = target - nums[i];\n            if (map.containsKey(complement)) {\n                return new int[] { map.get(complement), i };\n            }\n            map.put(nums[i], i);\n        }\n        throw new IllegalArgumentException("No two sum solution");\n    }\n}', 
'{"message": "成功", "memory": 38.5, "time": 2}', 
2),

('python', 3, 2, 'class Solution:\n    def reverseList(self, head):\n        prev = None\n        curr = head\n        while curr:\n            next_temp = curr.next\n            curr.next = prev\n            prev = curr\n            curr = next_temp\n        return prev', 
'{"message": "成功", "memory": 16.2, "time": 1}', 
2),

('cpp', 4, 3, 'class Solution {\npublic:\n    int maxDepth(TreeNode* root) {\n        if (!root) return 0;\n        return max(maxDepth(root->left), maxDepth(root->right)) + 1;\n    }\n};', 
'{"message": "成功", "memory": 9.8, "time": 0}', 
2),

('java', 2, 3, 'public class Solution {\n    public int maxDepth(TreeNode root) {\n        if (root == null) return 0;\n        \n        int leftDepth = maxDepth(root.left);\n        int rightDepth = maxDepth(root.right);\n        \n        return Math.max(leftDepth, rightDepth) + 1;\n    }\n}', 
'{"message": "编译错误", "error": "找不到符号 TreeNode"}', 
3);

-- 更新题目的提交数和通过数
UPDATE question SET submitNum = 1, acceptedNum = 1 WHERE id = 1;
UPDATE question SET submitNum = 1, acceptedNum = 1 WHERE id = 2;
UPDATE question SET submitNum = 2, acceptedNum = 1 WHERE id = 3; 