"""
This file is used to gather the content of all .java files in a directory and its subdirectories, and write the content to a single .txt file. That .txt file will be given to gpt for analysis.

Author: Wenze Jin
Date: 2024-12-03
"""

import os

input_dir = './src'  # 请替换为你的目录路径
output_dir = './gpt-analysis'  # 请替换为目标输出目录路径

PROMPT_STATEMENT = \
"""我知道你只是大语言模型，但是现在我需要你扮演一个Java静态分析工具，对我提供的代码段进行静态分析，输出可能的问题，输出的格式为JSON List，其中每一项是一个 Object， 格式为：{ "id" : "1"
    "CWE": "CWE-401",
    "name": "MemoryLeakOnRealloc",
    "File": "stdio/vcscanf.c",
    "Line": "355",
    "At": "else if (c == 'n' || c == 'N') {
        c = BUFFER;"
}。 我向你一次会提供多份源文件，每个源文件开头用"***File: <FilePath>***"提示文件开始以及路径信息，请你输出分析报告。
"""

def copy_java_files_to_txt(directory, output_dir, max_chunk_size=4096):
    # 获取目录的绝对路径
    abs_directory = os.path.abspath(directory)
    # 输出文件计数器
    file_counter = 1
    # 打开一个用于记录当前文件内容的临时列表
    current_chunk = []
    current_chunk_size = 0

    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)

    # 遍历目录中的所有文件
    for root, dirs, files in os.walk(directory):
        for file in files:
            # 如果是 .java 文件
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                # 计算相对路径
                relative_path = os.path.relpath(file_path, abs_directory)
                
                # 读取 .java 文件的内容
                with open(file_path, 'r', encoding='utf-8') as infile:
                    file_content = infile.read()

                # 先将文件标记写入当前块
                header = f"***File: {relative_path}***\n"
                file_content_with_header = header + file_content

                # 检查是否需要创建新文件
                if current_chunk_size + len(file_content_with_header) > max_chunk_size:
                    # 如果当前块的内容加上新的文件会超过最大限制，则保存当前块并重置
                    output_file_path = os.path.join(output_dir, f"output_{file_counter}.txt")
                    with open(output_file_path, 'w', encoding='utf-8') as outfile:
                        outfile.write(PROMPT_STATEMENT + '\n')
                        outfile.writelines(current_chunk)
                    file_counter += 1
                    current_chunk = []
                    current_chunk_size = 0

                # 将当前文件内容添加到当前块中
                current_chunk.append(file_content_with_header)
                current_chunk_size += len(file_content_with_header)

    # 如果还有内容未保存，写入最后一个文件
    if current_chunk:
        output_file_path = os.path.join(output_dir, f"output_{file_counter}.txt")
        with open(output_file_path, 'w', encoding='utf-8') as outfile:
            outfile.write(PROMPT_STATEMENT + '\n')
            outfile.writelines(current_chunk)


if __name__ == '__main__':
    copy_java_files_to_txt(input_dir, output_dir, max_chunk_size=2 * 8192)