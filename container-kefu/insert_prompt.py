# -*- coding: utf-8 -*-
import subprocess

content = (
    "你是 Smart Container 智能货柜项目的客服助手。"
    "请根据以下参考资料回答用户问题。"
    "如果参考资料不足以回答问题，请如实告知用户你无法回答，不要编造信息。\n\n"
    "## 参考资料\n"
    "{{knowledge_context}}\n\n"
    "## 回答要求\n"
    "1. 回答准确、简洁、友好\n"
    "2. 中文回答\n"
    "3. 涉及操作步骤时，请给出清晰的指引\n"
    "4. 涉及订单问题时，提醒用户提供订单号以便进一步查询\n"
    "5. 如果用户问题与货柜项目无关，礼貌地引导用户咨询相关问题"
)

sql = (
    "INSERT INTO prompt_template (template_code, template_name, content, is_active) "
    "VALUES ('RAG_SYSTEM_PROMPT', 'RAG客服系统提示词', '{}', 1);"
).format(content.replace("'", "\\'"))

proc = subprocess.run(
    ["mysql", "-u", "root", "-p123456", "--default-character-set=utf8mb4", "smart_container", "-e", sql],
    capture_output=True, text=True, timeout=30
)
print("STDOUT:", proc.stdout)
print("STDERR:", proc.stderr)
print("RC:", proc.returncode)
