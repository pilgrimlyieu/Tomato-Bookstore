<template>
  <div class="note-list">
    <div v-if="loading" class="w-full">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else>
      <div v-if="notes.length === 0" class="text-center py-8">
        <el-empty description="暂无读书笔记">
          <template #extra v-if="showCreateButton && productId">
            <el-button
              type="primary"
              class="rounded-lg"
              @click="handleCreateNote"
              >撰写笔记</el-button
            >
          </template>
        </el-empty>
      </div>

      <div v-else class="grid gap-6 md:grid-cols-2">
        <NoteCard
          v-for="note in notes"
          :key="note.id"
          :note="note"
          @view="$emit('view', note)"
          @edit="$emit('edit', note)"
          @delete="$emit('delete', note)"
        />
      </div>

      <div class="mt-6 text-center" v-if="showCreateButton && !hasUserNote && productId">
        <el-button type="primary" class="rounded-lg" @click="handleCreateNote">
          撰写读书笔记
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import type { Note } from "@/types/note";
import NoteCard from "./NoteCard.vue";

const props = defineProps<{
  notes: Note[];
  loading?: boolean;
  productId?: number;
  showCreateButton?: boolean;
}>();

const emit = defineEmits<{
  (e: "view", note: Note): void;
  (e: "edit", note: Note): void;
  (e: "delete", note: Note): void;
  (e: "create"): void;
}>();

// 用户 store
const userStore = useUserStore();

// 检查当前用户是否已经为当前产品创建了笔记
const hasUserNote = computed(() => {
  if (!props.productId || !userStore.user) return false;

  return props.notes.some(
    (note) =>
      note.userId === userStore.user?.id && note.productId === props.productId,
  );
});

// 创建笔记
const handleCreateNote = () => {
  emit("create");
};
</script>

<style scoped>
.note-list {
  width: 100%;
}
</style>
